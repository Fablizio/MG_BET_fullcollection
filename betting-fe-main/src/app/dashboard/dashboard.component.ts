import {Component, OnDestroy, OnInit,AfterViewInit} from '@angular/core';
import {DashboardService} from "../service/dashboard.service";

import * as am4core from "@amcharts/amcharts4/core";
import * as am4charts from "@amcharts/amcharts4/charts";
import am4themes_animated from "@amcharts/amcharts4/themes/animated";
import {TotalStatistics} from "../domain/total-statistics";

// Tema custom MG BET per avere testi chiari sui grafici
const mgDarkTheme = (target: any) => {
  // Cambia il "set" di colori di interfaccia (etichette, assi, legend, ecc.)
  if (target instanceof am4core.InterfaceColorSet) {
    target.setFor('text', am4core.color('#e5e7eb'));  // testi chiari
    target.setFor('grid', am4core.color('#4b5563'));  // griglia più soft
  }
};

// Usa il tema animato standard + il nostro tema dark
am4core.useTheme(am4themes_animated);
am4core.useTheme(mgDarkTheme);


type EsitoSegno = '1' | 'X' | '2';

interface MatchJson {
  id: string;
  dataEvent: string;
  team: string;
  uno: number;
  x: number;
  due: number;
  prediction: string;
  result: string;
  presa: boolean;
  quotaInizialeUno: number;
  quotaInizialeX: number;
  campionato: string;
  quotaInizialeDue: number;
  dataAggiornamento: string;
  predictionConfidence: number;
  predictionNote: string;
  percentualeUno: number;
  percentualeX: number;
  percentualeDue: number;
}

type MatchClassificazione = 'hit_secco' | 'hit_dc_only' | 'miss';

interface MatchEsteso extends MatchJson {
  esito: EsitoSegno | null;
  hasDoubleChance: boolean;
  seccoHit: boolean;
  doubleChanceHit: boolean;
  classificazione: MatchClassificazione;
}

interface CampionatoStats {
  id: string;
  territorio: string;
  nomeCampionato: string;
  totalePartite: number;
  vinteTotali: number;
  perse: number;
  winRate: number;
  vinteSecco: number;
  vinteSoloDoppia: number;
  mediaConfidence: number;
  mediaConfidenceVinte: number;
  mediaConfidencePerse: number;
}

interface CampionatiGroup {
  territorio: string;
  campionati: CampionatoStats[];
}

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css']
})
export class DashboardComponent implements OnInit, OnDestroy,AfterViewInit {
  constructor(private dashboardService: DashboardService) {
  }

  viewMode: 'totali' | 'raddoppi' = 'totali';

  // === DATI GREZZI (sostituisci con il tuo JSON reale / HTTP) ===
  matchesRaw: MatchJson[] = [];

  // ====== STRUTTURE DATI ======
  // --- Win rate per segno 1/X/2 (solo secco) ---
  segnoStats: Record<EsitoSegno, { total: number; hit: number; winRate: number }> = {
    '1': {total: 0, hit: 0, winRate: 0},
    'X': {total: 0, hit: 0, winRate: 0},
    '2': {total: 0, hit: 0, winRate: 0}
  };

// --- Fasce di confidenza ---
  confidenceBins: {
    label: string;
    from: number;
    to: number;
    total: number;
    hit: number;
    winRate: number;
  }[] = [];

// --- Andamento nel tempo ---
  timeBuckets: {
    date: Date;
    label: string;
    total: number;
    hit: number;
    winRate: number;
  }[] = [];

// --- Nuovi grafici ---
  private chartSegnoWinRate?: am4charts.XYChart3D;
  private chartConfWinRate?: am4charts.XYChart3D;
  private chartTimeTrend?: am4charts.XYChart;
  private chartCalibration?: am4charts.XYChart;
  // Bins per movimento quota (per campionato selezionato)
  campionatoOddsBins: {
    label: string;
    from: number;
    to: number;
    total: number;
    hit: number;
    winRate: number;
  }[] = [];

// Grafico movimento quote per campionato
  private chartCampionatoOdds?: am4charts.XYChart3D;


  // Liste partite per il campionato selezionato
  campionatoVinteSeccoMatches: MatchEsteso[] = [];
  campionatoVinteDoppiaMatches: MatchEsteso[] = [];

// Stato accordion
  seccoCollapsed = true;
  doppiaCollapsed = true;


  matches: MatchEsteso[] = [];

  campionatiStats: CampionatoStats[] = [];
  campionatiGroup: CampionatiGroup[] = [];

  selectedTerritorio: string | null = null;
  territorioCampionati: CampionatoStats[] = [];
  selectedCampionatoId: string | null = null;
  selectedCampionatoStats: CampionatoStats | null = null;

  // global
  globalTotalePartite = 0;
  globalVinte = 0;
  globalPerse = 0;
  globalWinRate = 0;

  // raddoppi
  raddoppiTotali = 0;
  raddoppiVinteSecco = 0;
  raddoppiVinteSoloDoppia = 0;
  raddoppiPerse = 0;

  // chart refs
  private chartGlobalWin?: am4charts.PieChart3D;
  private chartGlobalTipo?: am4charts.PieChart3D;
  private chartCampionatiBar?: am4charts.XYChart3D;
  private chartCampionatoBar?: am4charts.XYChart3D;
  private chartCampionatoPie?: am4charts.PieChart3D;
  private chartRaddoppiPie?: am4charts.PieChart3D;

  // ====== LIFECYCLE ======
  private dataLoaded = false;
  private viewInitialized = false;

  // === Statistiche raddoppi da service ===
  raddoppiTotalStatistics: TotalStatistics | null = null;
  private raddoppiStatsLoaded = false;
  private raddoppiStatsLoading = false;


  ngOnInit(): void {
    this.dashboardService.findAll()
      .subscribe(value => {
        this.matchesRaw = value || [];


        // 1) preparo i dati quando ho il JSON
        this.prepareData();
        this.dataLoaded = true;

        // 2) se la view è già pronta, disegno i grafici
        if (this.viewInitialized) {
          this.renderStaticCharts();
        }
      });
  }


  ngAfterViewInit(): void {
    this.viewInitialized = true;

    // se ho già caricato i dati, creo i grafici ora
    if (this.dataLoaded) {
      this.renderStaticCharts();
    }
  }


  ngOnDestroy(): void {
    this.disposeCharts();
  }

  // ================= LOGICA DATI =================

  private prepareData(): void {
    // deduplica per id
    const byId = new Map<string, MatchJson>();
    for (const m of this.matchesRaw) {
      byId.set(m.id, m);
    }
    this.matches = Array.from(byId.values()).map(m => this.enrichMatch(m));

    const statsTemp = new Map<string, {
      territorio: string;
      nomeCampionato: string;
      totalePartite: number;
      vinteTotali: number;
      perse: number;
      vinteSecco: number;
      vinteSoloDoppia: number;
      sumConfTot: number;
      sumConfVinte: number;
      sumConfPerse: number;
    }>();

    this.raddoppiTotali = 0;
    this.raddoppiVinteSecco = 0;
    this.raddoppiVinteSoloDoppia = 0;
    this.raddoppiPerse = 0;

    for (const m of this.matches) {
      const campionatoId = m.campionato;
      let territorio = campionatoId;
      let nomeCampionato = campionatoId;
      const parts = campionatoId.split(' - ');
      if (parts.length >= 2) {
        territorio = parts[0].trim();
        nomeCampionato = parts.slice(1).join(' - ').trim();
      }

      if (!statsTemp.has(campionatoId)) {
        statsTemp.set(campionatoId, {
          territorio,
          nomeCampionato,
          totalePartite: 0,
          vinteTotali: 0,
          perse: 0,
          vinteSecco: 0,
          vinteSoloDoppia: 0,
          sumConfTot: 0,
          sumConfVinte: 0,
          sumConfPerse: 0
        });
      }

      const s = statsTemp.get(campionatoId)!;
      s.totalePartite++;
      const conf = m.predictionConfidence || 0;
      s.sumConfTot += conf;

      const isHit = m.classificazione === 'hit_secco' || m.classificazione === 'hit_dc_only';
      if (isHit) {
        s.vinteTotali++;
        s.sumConfVinte += conf;
      } else {
        s.perse++;
        s.sumConfPerse += conf;
      }

      if (m.classificazione === 'hit_secco') {
        s.vinteSecco++;
      } else if (m.classificazione === 'hit_dc_only') {
        s.vinteSoloDoppia++;
      }

      // raddoppi globali
      if (m.hasDoubleChance) {
        this.raddoppiTotali++;
        if (m.classificazione === 'hit_secco') {
          this.raddoppiVinteSecco++;
        } else if (m.classificazione === 'hit_dc_only') {
          this.raddoppiVinteSoloDoppia++;
        } else {
          this.raddoppiPerse++;
        }
      }
    }

    this.campionatiStats = [];
    statsTemp.forEach((s, campionatoId) => {
      const stats: CampionatoStats = {
        id: campionatoId,
        territorio: s.territorio,
        nomeCampionato: s.nomeCampionato,
        totalePartite: s.totalePartite,
        vinteTotali: s.vinteTotali,
        perse: s.perse,
        winRate: s.totalePartite ? s.vinteTotali / s.totalePartite : 0,
        vinteSecco: s.vinteSecco,
        vinteSoloDoppia: s.vinteSoloDoppia,
        mediaConfidence: s.totalePartite ? s.sumConfTot / s.totalePartite : 0,
        mediaConfidenceVinte: s.vinteTotali ? s.sumConfVinte / s.vinteTotali : 0,
        mediaConfidencePerse: s.perse ? s.sumConfPerse / s.perse : 0
      };
      this.campionatiStats.push(stats);
    });

    // global
    this.globalTotalePartite = this.matches.length;
    this.globalVinte = this.matches.filter(m => m.classificazione !== 'miss').length;
    this.globalPerse = this.globalTotalePartite - this.globalVinte;
    this.globalWinRate = this.globalTotalePartite
      ? this.globalVinte / this.globalTotalePartite
      : 0;

    this.buildCampionatiGroup();

    // 6) statistiche avanzate globali
    this.computeSegnoStats();
    this.computeConfidenceBins();
    this.computeTimeBuckets();

  }


  private computeCampionatoOddsBins(matchesCampionato: MatchEsteso[]): void {
    const bins = [
      {label: 'Quota crollata (≤ -20%)', from: -1000, to: -20},
      {label: '-20% / -10%', from: -20, to: -10},
      {label: '-10% / -5%', from: -10, to: -5},
      {label: 'Stabile (±5%)', from: -5, to: 5},
      {label: '+5% / +10%', from: 5, to: 10},
      {label: 'Quota salita (≥ +10%)', from: 10, to: 1000}
    ].map(b => ({...b, total: 0, hit: 0, winRate: 0}));

    for (const m of matchesCampionato) {
      const {main} = this.parsePrediction(m.prediction);
      // consideriamo solo i pronostici con segno secco 1/X/2
      if (main !== '1' && main !== 'X' && main !== '2') {
        continue;
      }

      let quotaInit: number | null = null;
      let quotaFinal: number | null = null;

      if (main === '1') {
        quotaInit = m.quotaInizialeUno;
        quotaFinal = m.uno;
      } else if (main === 'X') {
        quotaInit = m.quotaInizialeX;
        quotaFinal = m.x;
      } else if (main === '2') {
        quotaInit = m.quotaInizialeDue;
        quotaFinal = m.due;
      }

      if (!quotaInit || !quotaFinal || quotaInit <= 0) {
        continue;
      }

      const changePerc = (quotaFinal / quotaInit - 1) * 100;

      const bin = bins.find(b => changePerc >= b.from && changePerc < b.to);
      if (!bin) {
        continue;
      }

      bin.total++;
      const isHit = m.classificazione === 'hit_secco' || m.classificazione === 'hit_dc_only';
      if (isHit) {
        bin.hit++;
      }
    }

    for (const b of bins) {
      b.winRate = b.total ? b.hit / b.total : 0;
    }

    this.campionatoOddsBins = bins;
  }

  private createCampionatoOddsChart(): void {
    if (this.chartCampionatoOdds) {
      this.chartCampionatoOdds.dispose();
    }

    const chart = am4core.create('chartCampionatoOdds', am4charts.XYChart3D);

    chart.data = this.campionatoOddsBins
      .filter(b => b.total > 0)
      .map(b => ({
        fascia: b.label,
        winRate: +(b.winRate * 100).toFixed(2),
        total: b.total
      }));

    const categoryAxis = chart.xAxes.push(new am4charts.CategoryAxis());
    categoryAxis.dataFields.category = 'fascia';
    categoryAxis.renderer.labels.template.rotation = 315;
    categoryAxis.renderer.labels.template.horizontalCenter = 'right';
    categoryAxis.renderer.labels.template.verticalCenter = 'middle';
    categoryAxis.renderer.minGridDistance = 20;

    const valueAxis = chart.yAxes.push(new am4charts.ValueAxis());
    valueAxis.title.text = 'Win rate (%)';
    valueAxis.min = 0;
    valueAxis.max = 100;

    const series = chart.series.push(new am4charts.ColumnSeries3D());
    series.dataFields.categoryX = 'fascia';
    series.dataFields.valueY = 'winRate';
    series.columns.template.tooltipText =
      '{categoryX}: {valueY.formatNumber("#.0")}% ({total} partite)';

    this.chartCampionatoOdds = chart;
  }


  private createCalibrationChart(): void {
    if (this.chartCalibration) {
      this.chartCalibration.dispose();
    }

    const chart = am4core.create('chartCalibration', am4charts.XYChart);

    // Punti osservati (per fascia)
    const data = this.confidenceBins
      .filter(b => b.total > 0)
      .map(b => {
        const mid = (b.from + b.to) / 2;
        return {
          predicted: mid,
          observed: +(b.winRate * 100).toFixed(2),
          label: b.label,
          total: b.total
        };
      });

    chart.data = data;

    // Assi
    const xAxis = chart.xAxes.push(new am4charts.ValueAxis());
    xAxis.title.text = 'Probabilità prevista (%)';
    xAxis.min = 0;
    xAxis.max = 100;

    const yAxis = chart.yAxes.push(new am4charts.ValueAxis());
    yAxis.title.text = 'Probabilità osservata (%)';
    yAxis.min = 0;
    yAxis.max = 100;

    // Linea dei punti osservati
    const observedSeries = chart.series.push(new am4charts.LineSeries());
    observedSeries.dataFields.valueX = 'predicted';
    observedSeries.dataFields.valueY = 'observed';
    observedSeries.name = 'Modello';
    observedSeries.tooltipText =
      '{label}: pred. {predicted.formatNumber("#.0")}% → obs. {observed.formatNumber("#.0")}% ({total} partite)';
    observedSeries.strokeWidth = 3;

    const bullet = observedSeries.bullets.push(new am4charts.CircleBullet());
    bullet.circle.radius = 4;

    // Linea diagonale perfetta (y = x)
    const idealSeries = chart.series.push(new am4charts.LineSeries());
    idealSeries.data = [
      {predicted: 0, observed: 0},
      {predicted: 100, observed: 100}
    ];
    idealSeries.dataFields.valueX = 'predicted';
    idealSeries.dataFields.valueY = 'observed';
    idealSeries.name = 'Perfettamente calibrato';
    idealSeries.strokeDasharray = '4,4';
    idealSeries.strokeOpacity = 0.7;

    chart.legend = new am4charts.Legend();
    chart.cursor = new am4charts.XYCursor();

    this.chartCalibration = chart;
  }


  private computeSegnoStats(): void {
    this.segnoStats = {
      '1': {total: 0, hit: 0, winRate: 0},
      'X': {total: 0, hit: 0, winRate: 0},
      '2': {total: 0, hit: 0, winRate: 0}
    };

    for (const m of this.matches) {
      const {main} = this.parsePrediction(m.prediction);
      if (main === '1' || main === 'X' || main === '2') {
        const s = this.segnoStats[main];
        s.total++;
        // qui consideriamo "hit" solo se il segno secco è stato preso
        if (m.classificazione === 'hit_secco') {
          s.hit++;
        }
      }
    }

    (['1', 'X', '2'] as EsitoSegno[]).forEach(segno => {
      const s = this.segnoStats[segno];
      s.winRate = s.total ? s.hit / s.total : 0;
    });
  }

  private computeConfidenceBins(): void {
    const bins = [
      {label: '0–20%', from: 0, to: 20},
      {label: '20–40%', from: 20, to: 40},
      {label: '40–60%', from: 40, to: 60},
      {label: '60–80%', from: 60, to: 80},
      {label: '80–100%', from: 80, to: 101}
    ].map(b => ({...b, total: 0, hit: 0, winRate: 0}));

    for (const m of this.matches) {
      const conf = m.predictionConfidence ? m.predictionConfidence : 0;
      const isHit = m.classificazione === 'hit_secco' || m.classificazione === 'hit_dc_only';
      const bin = bins.find(b => conf >= b.from && conf < b.to);
      if (!bin) {
        continue;
      }
      bin.total++;
      if (isHit) {
        bin.hit++;
      }
    }

    for (const b of bins) {
      b.winRate = b.total ? b.hit / b.total : 0;
    }

    this.confidenceBins = bins;
  }


  private buildCampionatiGroup(): void {
    const byTerritorio = new Map<string, CampionatoStats[]>();

    for (const s of this.campionatiStats) {
      if (!byTerritorio.has(s.territorio)) {
        byTerritorio.set(s.territorio, []);
      }
      byTerritorio.get(s.territorio)!.push(s);
    }

    this.campionatiGroup = [];
    byTerritorio.forEach((campionati, territorio) => {
      campionati.sort((a, b) => a.nomeCampionato.localeCompare(b.nomeCampionato));
      this.campionatiGroup.push({territorio, campionati});
    });

    this.campionatiGroup.sort((a, b) => a.territorio.localeCompare(b.territorio));
    this.territorioCampionati = [];
    this.selectedTerritorio = null;
    this.selectedCampionatoId = null;
    this.selectedCampionatoStats = null;
  }

  private computeTimeBuckets(): void {
    const map = new Map<string, { date: Date; total: number; hit: number }>();

    for (const m of this.matches) {
      const d = this.parseDataEventToDate(m.dataEvent);
      if (!d) {
        continue;
      }

      const key =
        `${d.getFullYear()}-${(d.getMonth() + 1).toString().padStart(2, '0')}-${d.getDate().toString().padStart(2, '0')}`;

      if (!map.has(key)) {
        map.set(key, {
          date: new Date(d.getFullYear(), d.getMonth(), d.getDate()),
          total: 0,
          hit: 0
        });
      }

      const bucket = map.get(key)!;
      bucket.total++;
      if (m.classificazione === 'hit_secco' || m.classificazione === 'hit_dc_only') {
        bucket.hit++;
      }
    }

    const buckets: {
      date: Date;
      label: string;
      total: number;
      hit: number;
      winRate: number;
    }[] = [];

    map.forEach((v) => {
      const winRate = v.total ? v.hit / v.total : 0;
      const day = v.date.getDate().toString().padStart(2, '0');
      const month = (v.date.getMonth() + 1).toString().padStart(2, '0');
      const year = v.date.getFullYear();
      const label = `${day}/${month}/${year}`;

      buckets.push({
        date: v.date,
        label,
        total: v.total,
        hit: v.hit,
        winRate
      });
    });

    buckets.sort((a, b) => a.date.getTime() - b.date.getTime());
    this.timeBuckets = buckets;
  }

  private createSegnoWinRateChart(): void {
    if (this.chartSegnoWinRate) {
      this.chartSegnoWinRate.dispose();
    }

    const chart = am4core.create('chartSegnoWinRate', am4charts.XYChart3D);

    chart.data = (['1', 'X', '2'] as EsitoSegno[]).map(segno => ({
      segno,
      winRate: +(this.segnoStats[segno].winRate * 100).toFixed(2),
      total: this.segnoStats[segno].total
    }));

    const categoryAxis = chart.xAxes.push(new am4charts.CategoryAxis());
    categoryAxis.dataFields.category = 'segno';
    categoryAxis.renderer.minGridDistance = 20;

    const valueAxis = chart.yAxes.push(new am4charts.ValueAxis());
    valueAxis.min = 0;
    valueAxis.max = 100;
    valueAxis.title.text = 'Win rate (%)';

    const series = chart.series.push(new am4charts.ColumnSeries3D());
    series.dataFields.valueY = 'winRate';
    series.dataFields.categoryX = 'segno';
    series.columns.template.tooltipText =
      'Segno {categoryX}: {valueY.formatNumber("#.0")}% ({total} pronostici)';

    this.chartSegnoWinRate = chart;
  }

  private createConfidenceWinRateChart(): void {
    if (this.chartConfWinRate) {
      this.chartConfWinRate.dispose();
    }

    const chart = am4core.create('chartConfWinRate', am4charts.XYChart3D);

    chart.data = this.confidenceBins.map(b => ({
      fascia: b.label,
      winRate: +(b.winRate * 100).toFixed(2),
      total: b.total
    }));

    const categoryAxis = chart.xAxes.push(new am4charts.CategoryAxis());
    categoryAxis.dataFields.category = 'fascia';
    categoryAxis.renderer.minGridDistance = 20;
    categoryAxis.renderer.labels.template.rotation = 315;
    categoryAxis.renderer.labels.template.horizontalCenter = 'right';
    categoryAxis.renderer.labels.template.verticalCenter = 'middle';

    const valueAxis = chart.yAxes.push(new am4charts.ValueAxis());
    valueAxis.min = 0;
    valueAxis.max = 100;
    valueAxis.title.text = 'Win rate (%)';

    const series = chart.series.push(new am4charts.ColumnSeries3D());
    series.dataFields.valueY = 'winRate';
    series.dataFields.categoryX = 'fascia';
    series.columns.template.tooltipText =
      '{categoryX}: {valueY.formatNumber("#.0")}% ({total} partite)';

    this.chartConfWinRate = chart;
  }

  private createTimeTrendChart(): void {
    if (this.chartTimeTrend) {
      this.chartTimeTrend.dispose();
    }

    const chart = am4core.create('chartTimeTrendWinRate', am4charts.XYChart);

    chart.data = this.timeBuckets.map(b => ({
      date: b.date,
      winRate: +(b.winRate * 100).toFixed(2),
      total: b.total
    }));

    const dateAxis = chart.xAxes.push(new am4charts.DateAxis());
    dateAxis.renderer.minGridDistance = 50;
    dateAxis.dateFormats.setKey('day', 'dd/MM');
    dateAxis.tooltipDateFormat = 'dd/MM/yyyy';

    const valueAxis = chart.yAxes.push(new am4charts.ValueAxis());
    valueAxis.title.text = 'Win rate (%)';
    valueAxis.min = 0;
    valueAxis.max = 100;

    const series = chart.series.push(new am4charts.LineSeries());
    series.dataFields.dateX = 'date';
    series.dataFields.valueY = 'winRate';
    series.tooltipText = '{valueY.formatNumber("#.0")}% ({total} partite)';
    series.strokeWidth = 2;

    chart.cursor = new am4charts.XYCursor();
    chart.cursor.behavior = 'panX';
    chart.cursor.xAxis = dateAxis;
    chart.scrollbarX = new am4core.Scrollbar();

    this.chartTimeTrend = chart;
  }


  private parseDataEventToDate(dataEvent: string): Date | null {
    if (!dataEvent) {
      return null;
    }
    // formato atteso: "dd/MM/yyyy HH:mm"
    const m = dataEvent.match(/^(\d{2})\/(\d{2})\/(\d{4})\s+(\d{2}):(\d{2})$/);
    if (!m) {
      return null;
    }

    const day = parseInt(m[1], 10);
    const month = parseInt(m[2], 10) - 1;
    const year = parseInt(m[3], 10);
    const hour = parseInt(m[4], 10);
    const minute = parseInt(m[5], 10);

    return new Date(year, month, day, hour, minute);
  }


  private getEsitoFromResult(result: string): EsitoSegno | null {
    if (!result) {
      return null;
    }
    const m = result.match(/^\s*(\d+)\s*:\s*(\d+)\s*$/);
    if (!m) {
      return null;
    }
    const home = parseInt(m[1], 10);
    const away = parseInt(m[2], 10);
    if (home > away) {
      return '1';
    }
    if (away > home) {
      return '2';
    }
    return 'X';
  }

  private parsePrediction(prediction: string): { main: string | null; doubleChance: string | null } {
    if (!prediction) {
      return {main: null, doubleChance: null};
    }
    const parts = prediction.split(/oppure/i).map(p => p.trim()).filter(p => !!p);
    const main = parts.length > 0 ? parts[0] : null;

    let doubleChance: string | null = null;
    for (const p of parts) {
      if (p === '1X' || p === 'X2' || p === '12') {
        doubleChance = p;
      }
    }
    return {main, doubleChance};
  }

  private enrichMatch(m: MatchJson): MatchEsteso {
    const esito = this.getEsitoFromResult(m.result);
    const {main, doubleChance} = this.parsePrediction(m.prediction);

    const mainSingle = main === '1' || main === 'X' || main === '2';
    const mainIsDC = main === '1X' || main === 'X2' || main === '12';

    const hasDoubleChance = !!(doubleChance || mainIsDC);

    const seccoHit = !!(esito && mainSingle && esito === main);

    let dc = doubleChance;
    if (!dc && mainIsDC) {
      dc = main!;
    }

    const doubleChanceHit = !!(esito && dc && dc.includes(esito));

    let classificazione: MatchClassificazione;
    if (seccoHit) {
      classificazione = 'hit_secco';
    } else if (doubleChanceHit) {
      classificazione = 'hit_dc_only';
    } else {
      classificazione = 'miss';
    }

    return {
      ...m,
      esito,
      hasDoubleChance,
      seccoHit,
      doubleChanceHit,
      classificazione
    };
  }

  // ================== GRAFICI ==================

  private renderStaticCharts(): void {
    this.createGlobalWinPie();
    this.createGlobalTipoPie();
    this.createCampionatiBar();
   // this.createRaddoppiPie();
    this.createSegnoWinRateChart();
    this.createConfidenceWinRateChart();
    this.createTimeTrendChart();
    this.createCalibrationChart();
  }

  private disposeCharts(): void {
    if (this.chartGlobalWin) {
      this.chartGlobalWin.dispose();
    }
    if (this.chartGlobalTipo) {
      this.chartGlobalTipo.dispose();
    }
    if (this.chartCampionatiBar) {
      this.chartCampionatiBar.dispose();
    }
    if (this.chartCampionatoBar) {
      this.chartCampionatoBar.dispose();
    }
    if (this.chartCampionatoPie) {
      this.chartCampionatoPie.dispose();
    }
    if (this.chartRaddoppiPie) {
      this.chartRaddoppiPie.dispose();
    }

    if (this.chartSegnoWinRate) {
      this.chartSegnoWinRate.dispose();
    }
    if (this.chartConfWinRate) {
      this.chartConfWinRate.dispose();
    }
    if (this.chartTimeTrend) {
      this.chartTimeTrend.dispose();
    }
    if (this.chartCalibration) {
      this.chartCalibration.dispose();
    }

    if (this.chartCampionatoOdds) {
      this.chartCampionatoOdds.dispose();
    }

  }

  private createGlobalWinPie(): void {
    if (this.chartGlobalWin) {
      this.chartGlobalWin.dispose();
    }

    const chart = am4core.create('chartGlobalWin', am4charts.PieChart3D);
    chart.innerRadius = am4core.percent(55);

    chart.data = [
      {categoria: 'Predizioni prese', valore: this.globalVinte},
      {categoria: 'Predizioni sbagliate', valore: this.globalPerse}
    ];

    const series = chart.series.push(new am4charts.PieSeries3D());
    series.dataFields.value = 'valore';
    series.dataFields.category = 'categoria';
    series.labels.template.text = '{category}';
    series.slices.template.tooltipText =
      '{category}: {value} ({value.percent.formatNumber("#.0")}%)';

    chart.legend = new am4charts.Legend();

    this.chartGlobalWin = chart;
  }

  private createGlobalTipoPie(): void {
    if (this.chartGlobalTipo) {
      this.chartGlobalTipo.dispose();
    }

    const secco = this.matches.filter(m => m.classificazione === 'hit_secco').length;
    const dcOnly = this.matches.filter(m => m.classificazione === 'hit_dc_only').length;
    const miss = this.matches.filter(m => m.classificazione === 'miss').length;

    const chart = am4core.create('chartGlobalTipo', am4charts.PieChart3D);
    chart.innerRadius = am4core.percent(55);

    chart.data = [
      {categoria: 'Vinte segno secco', valore: secco},
      {categoria: 'Vinte solo doppia chance', valore: dcOnly},
      {categoria: 'Perse', valore: miss}
    ];

    const series = chart.series.push(new am4charts.PieSeries3D());
    series.dataFields.value = 'valore';
    series.dataFields.category = 'categoria';
    series.labels.template.text = '{category}';
    series.slices.template.tooltipText =
      '{category}: {value} ({value.percent.formatNumber("#.0")}%)';

    chart.legend = new am4charts.Legend();

    this.chartGlobalTipo = chart;
  }

  private createCampionatiBar(): void {
    if (this.chartCampionatiBar) {
      this.chartCampionatiBar.dispose();
    }

    const chart = am4core.create('chartCampionatiBar', am4charts.XYChart3D);

    chart.data = this.campionatiStats.map(s => ({
      campionato: `${s.territorio} - ${s.nomeCampionato}`,
      winRate: +(s.winRate * 100).toFixed(2)
    }));

    const categoryAxis = chart.xAxes.push(new am4charts.CategoryAxis());
    categoryAxis.dataFields.category = 'campionato';
    categoryAxis.renderer.minGridDistance = 20;
    categoryAxis.renderer.labels.template.rotation = 315;
    categoryAxis.renderer.labels.template.horizontalCenter = 'right';
    categoryAxis.renderer.labels.template.verticalCenter = 'middle';

    const valueAxis = chart.yAxes.push(new am4charts.ValueAxis());
    valueAxis.title.text = 'Win rate (%)';
    valueAxis.min = 0;
    valueAxis.max = 100;

    const series = chart.series.push(new am4charts.ColumnSeries3D());
    series.dataFields.valueY = 'winRate';
    series.dataFields.categoryX = 'campionato';
    series.name = 'Win rate';
    series.columns.template.tooltipText = '{categoryX}: {valueY.formatNumber("#.0")}%';

    this.chartCampionatiBar = chart;
  }

  private createCampionatoBar(stats: CampionatoStats): void {
    if (this.chartCampionatoBar) {
      this.chartCampionatoBar.dispose();
    }

    const chart = am4core.create('chartCampionatoDettaglioBar', am4charts.XYChart3D);

    chart.data = [
      {categoria: 'Vinte segno secco', valore: stats.vinteSecco},
      {categoria: 'Vinte solo doppia chance', valore: stats.vinteSoloDoppia},
      {categoria: 'Perse', valore: stats.perse}
    ];

    const categoryAxis = chart.xAxes.push(new am4charts.CategoryAxis());
    categoryAxis.dataFields.category = 'categoria';
    categoryAxis.renderer.minGridDistance = 20;

    const valueAxis = chart.yAxes.push(new am4charts.ValueAxis());
    valueAxis.title.text = 'Numero partite';
    valueAxis.min = 0;

    const series = chart.series.push(new am4charts.ColumnSeries3D());
    series.dataFields.valueY = 'valore';
    series.dataFields.categoryX = 'categoria';
    series.columns.template.tooltipText = '{categoryX}: {valueY}';

    this.chartCampionatoBar = chart;
  }

  private createCampionatoPie(stats: CampionatoStats): void {
    if (this.chartCampionatoPie) {
      this.chartCampionatoPie.dispose();
    }

    const chart = am4core.create('chartCampionatoPie', am4charts.PieChart3D);
    chart.innerRadius = am4core.percent(55);

    chart.data = [
      {categoria: 'Vinte segno secco', valore: stats.vinteSecco},
      {categoria: 'Vinte solo doppia chance', valore: stats.vinteSoloDoppia},
      {categoria: 'Perse', valore: stats.perse}
    ];

    const series = chart.series.push(new am4charts.PieSeries3D());
    series.dataFields.value = 'valore';
    series.dataFields.category = 'categoria';
    series.slices.template.tooltipText =
      '{category}: {value} ({value.percent.formatNumber("#.0")}%)';

    chart.legend = new am4charts.Legend();

    this.chartCampionatoPie = chart;
  }

  private createRaddoppiPie(): void {
    if (this.chartRaddoppiPie) {
      this.chartRaddoppiPie.dispose();
    }

    const chart = am4core.create('chartRaddoppiPie', am4charts.PieChart3D);
    chart.innerRadius = am4core.percent(55);

    // di default uso i dati calcolati localmente
    let win = this.raddoppiVinteSecco + this.raddoppiVinteSoloDoppia;
    let lose = this.raddoppiPerse;

    // se ho i dati dal service, li sovrascrivo
    if (this.raddoppiTotalStatistics) {
      win = this.raddoppiTotalStatistics.win;
      lose = this.raddoppiTotalStatistics.lose;
    }

    chart.data = [
      { categoria: 'Raddoppi vinti', valore: win },
      { categoria: 'Raddoppi persi', valore: lose }
    ];

    const series = chart.series.push(new am4charts.PieSeries3D());
    series.dataFields.value = 'valore';
    series.dataFields.category = 'categoria';
    series.slices.template.tooltipText =
      '{category}: {value} ({value.percent.formatNumber("#.0")}%)';

    chart.legend = new am4charts.Legend();

    this.chartRaddoppiPie = chart;
  }


  // =============== HANDLER UI (select) ===============

  onTerritorioChange(): void {
    const group =
      this.campionatiGroup.find(g => g.territorio === this.selectedTerritorio) || null;

    this.territorioCampionati = group ? group.campionati : [];
    this.selectedCampionatoId = null;
    this.selectedCampionatoStats = null;

    // reset liste partite
    this.campionatoVinteSeccoMatches = [];
    this.campionatoVinteDoppiaMatches = [];
    this.campionatoOddsBins = [];
    this.seccoCollapsed = true;
    this.doppiaCollapsed = true;

    // distruggi grafici campionato
    if (this.chartCampionatoBar) {
      this.chartCampionatoBar.dispose();
      this.chartCampionatoBar = undefined;
    }
    if (this.chartCampionatoPie) {
      this.chartCampionatoPie.dispose();
      this.chartCampionatoPie = undefined;
    }
    if (this.chartCampionatoOdds) {
      this.chartCampionatoOdds.dispose();
      this.chartCampionatoOdds = undefined;
    }
  }


  onCampionatoChange(): void {
    if (!this.selectedCampionatoId) {
      this.selectedCampionatoStats = null;

      this.campionatoVinteSeccoMatches = [];
      this.campionatoVinteDoppiaMatches = [];
      this.campionatoOddsBins = [];
      this.seccoCollapsed = true;
      this.doppiaCollapsed = true;

      if (this.chartCampionatoBar) {
        this.chartCampionatoBar.dispose();
        this.chartCampionatoBar = undefined;
      }
      if (this.chartCampionatoPie) {
        this.chartCampionatoPie.dispose();
        this.chartCampionatoPie = undefined;
      }
      if (this.chartCampionatoOdds) {
        this.chartCampionatoOdds.dispose();
        this.chartCampionatoOdds = undefined;
      }

      return;
    }

    const stats =
      this.territorioCampionati.find(c => c.id === this.selectedCampionatoId) || null;

    this.selectedCampionatoStats = stats;

    if (stats) {
      const campionatoId = stats.id;
      const matchesCampionato = this.matches.filter(m => m.campionato === campionatoId);

      // Grafici principali
      this.createCampionatoBar(stats);
      this.createCampionatoPie(stats);

      // Tabelle secco / doppia
      this.campionatoVinteSeccoMatches = matchesCampionato.filter(
        m => m.classificazione === 'hit_secco'
      );
      this.campionatoVinteDoppiaMatches = matchesCampionato.filter(
        m => m.classificazione === 'hit_dc_only'
      );
      this.seccoCollapsed = true;
      this.doppiaCollapsed = true;

      // Movimento quote sul segno secco
      this.computeCampionatoOddsBins(matchesCampionato);
      this.createCampionatoOddsChart();
    }
  }

  setViewMode(mode: 'totali' | 'raddoppi'): void {
    this.viewMode = mode;

    if (mode === 'raddoppi') {
      this.ensureRaddoppiStats();
    }
  }
  private ensureRaddoppiStats(): void {
    // se già caricate, ridisegno solo il grafico
    if (this.raddoppiStatsLoaded) {
      this.createRaddoppiPie();
      return;
    }

    // se una chiamata è già in corso, non rifaccio la request
    if (this.raddoppiStatsLoading) {
      return;
    }

    this.raddoppiStatsLoading = true;

    this.dashboardService.getTotalStatisticsRaddoppi()
      .subscribe({
        next: stats => {
          this.raddoppiStatsLoading = false;
          this.raddoppiStatsLoaded = true;
          this.raddoppiTotalStatistics = stats;
          this.createRaddoppiPie();     // disegna il grafico coi dati del service
        },
        error: err => {
          this.raddoppiStatsLoading = false;
          console.error('Errore nel caricamento delle statistiche raddoppi', err);
          // fallback sui dati locali, se vuoi
          this.createRaddoppiPie();
        }
      });
  }


}
