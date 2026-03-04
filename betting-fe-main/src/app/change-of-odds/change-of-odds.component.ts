import { Component, OnInit, AfterViewInit, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { DashboardService } from '../service/dashboard.service';
import * as am4core from '@amcharts/amcharts4/core';
import * as am4charts from '@amcharts/amcharts4/charts';
import am4themes_animated from '@amcharts/amcharts4/themes/animated';
import { NgxUiLoaderService } from 'ngx-ui-loader';

@Component({
  selector: 'app-change-of-odds',
  templateUrl: './change-of-odds.component.html',
  styleUrls: ['./change-of-odds.component.css']
})
export class ChangeOfOddsComponent implements OnInit, AfterViewInit, OnDestroy {

  idOdd: string;
  nomeSquadra: string;

  /** Manteniamo i riferimenti ai grafici per poterli distruggere correttamente */
  private charts: { [key: string]: am4charts.XYChart } = {};

  constructor(
    private activatedRouter: ActivatedRoute,
    private spinner: NgxUiLoaderService,
    private dashboardService: DashboardService
  ) {
    // Tema animato di amCharts (una sola volta)
    am4core.useTheme(am4themes_animated);
  }

  ngOnInit(): void {
    this.idOdd = this.activatedRouter.snapshot.params.idOdd;
    this.nomeSquadra = this.activatedRouter.snapshot.params.nomeSquadra;
  }

  ngAfterViewInit(): void {
    this.getGraph();
  }

  ngOnDestroy(): void {
    // Evitiamo memory leak dei grafici
    Object.values(this.charts).forEach(chart => {
      if (chart) {
        chart.dispose();
      }
    });
    this.charts = {};
  }

  getGraph(): void {
    this.spinner.start();

    this.dashboardService.getChangeOfOdds(this.idOdd)
      .subscribe(
        (rs: ChangeOfOdds[]) => {
          this.createChart('chartdivUno', rs, 'uno');
          this.createChart('chartdivX', rs, 'x');
          this.createChart('chartdivDue', rs, 'due');
          this.spinner.stop();
        },
        () => this.spinner.stop()
      );
  }

  /**
   * Crea un grafico XY in stile dark MG per il tipo quota (1 / X / 2)
   */
  private createChart(
    idGrafico: string,
    values: ChangeOfOdds[],
    typeOdd: 'uno' | 'x' | 'due'
  ): void {

    // Se esiste già un grafico su quel div, lo distruggiamo
    if (this.charts[idGrafico]) {
      this.charts[idGrafico].dispose();
      delete this.charts[idGrafico];
    }

    // Colori personalizzati per tipo quota
    const colorMap: Record<typeof typeOdd, am4core.Color> = {
      uno: am4core.color('#4ade80'),  // verde
      x:   am4core.color('#facc15'),  // giallo
      due: am4core.color('#fb7185')   // rosso soft
    };
    const mainColor = colorMap[typeOdd];

    const chart = am4core.create(idGrafico, am4charts.XYChart);

    // Layout / background in linea con il tema MG
    chart.padding(10, 15, 10, 10);
    chart.background.fill = am4core.color('#020617');
    chart.background.fillOpacity = 1;
    chart.plotContainer.background.fill = am4core.color('#020617');
    chart.plotContainer.background.fillOpacity = 1;


    // Data
    const data: { date: Date; value: number }[] = [];

    values.forEach(rs => {
      if (typeOdd === 'uno') {
        data.push({ date: new Date(rs.dateTime), value: rs.uno });
      } else if (typeOdd === 'x') {
        data.push({ date: new Date(rs.dateTime), value: rs.x });
      } else {
        data.push({ date: new Date(rs.dateTime), value: rs.due });
      }
    });

    // Ordiniamo per data
    data.sort((a, b) => a.date.getTime() - b.date.getTime());
    chart.data = data;

    // Asse X (tempo)
    const dateAxis = chart.xAxes.push(new am4charts.DateAxis());
    dateAxis.baseInterval = { timeUnit: 'minute', count: 1 };
    dateAxis.tooltipDateFormat = 'HH:mm, d MMMM';
    dateAxis.renderer.grid.template.stroke = am4core.color('#374151');
    dateAxis.renderer.grid.template.strokeOpacity = 0.3;
    dateAxis.renderer.labels.template.fill = am4core.color('#9ca3af');
    dateAxis.renderer.labels.template.fontSize = 11;
    dateAxis.renderer.minGridDistance = 60;
    dateAxis.renderer.line.stroke = am4core.color('#4b5563');
    dateAxis.renderer.line.strokeOpacity = 0.8;

    // Mostra tutta la serie, nessuno zoom iniziale
    dateAxis.start = 0;
    dateAxis.end = 1;
    dateAxis.keepSelection = false;

    // Asse Y (quota)
    const valueAxis = chart.yAxes.push(new am4charts.ValueAxis());
    valueAxis.tooltip.disabled = true;
    valueAxis.renderer.grid.template.stroke = am4core.color('#374151');
    valueAxis.renderer.grid.template.strokeOpacity = 0.3;
    valueAxis.renderer.labels.template.fill = am4core.color('#9ca3af');
    valueAxis.renderer.labels.template.fontSize = 11;
    valueAxis.renderer.minGridDistance = 20;
    valueAxis.renderer.ticks.template.disabled = true;
    valueAxis.renderer.line.stroke = am4core.color('#4b5563');
    valueAxis.renderer.line.strokeOpacity = 0.8;

    if (typeOdd === 'uno') {
      valueAxis.title.text = 'Quota 1';
    } else if (typeOdd === 'x') {
      valueAxis.title.text = 'Quota X';
    } else {
      valueAxis.title.text = 'Quota 2';
    }
    valueAxis.title.fill = am4core.color('#e5e7eb');
    valueAxis.title.fontSize = 12;

    // Serie linea
    const series = chart.series.push(new am4charts.LineSeries());
    series.dataFields.dateX = 'date';
    series.dataFields.valueY = 'value';
    series.tooltipText = 'Quota: [bold]{valueY}[/]';
    series.stroke = mainColor;
    series.fill = mainColor;
    series.strokeWidth = 2;
    series.fillOpacity = 0.12;
    series.minBulletDistance = 10; // meno “rumore” visivo

    // Bullet sul grafico principale
    const bullet = series.bullets.push(new am4charts.CircleBullet());
    bullet.circle.radius = 3.5;
    bullet.circle.fill = am4core.color('#020617');
    bullet.circle.stroke = mainColor;
    bullet.circle.strokeWidth = 2;

    // Tooltip dark
    series.tooltip.background.fill = am4core.color('#020617');
    series.tooltip.background.stroke = mainColor;
    series.tooltip.background.cornerRadius = 8;
    series.tooltip.getFillFromObject = false;
    series.tooltip.getStrokeFromObject = false;
    series.tooltip.label.fill = am4core.color('#e5e7eb');

    // Cursor: solo per vedere il tooltip, niente pan/zoom
    const cursor = new am4charts.XYCursor();
    cursor.behavior = 'none';
    cursor.lineY.opacity = 0;
    cursor.lineX.stroke = am4core.color('#6b7280');
    cursor.lineX.strokeWidth = 1;
    chart.cursor = cursor;

    // chart.scrollbarX = null;

    chart.responsive.enabled = true;

    this.charts[idGrafico] = chart;
  }

}

