import {Component, OnInit} from '@angular/core';
import {BettingService} from '../betting.service';
import * as am4core from '@amcharts/amcharts4/core';
import * as am4charts from '@amcharts/amcharts4/charts';
import {TotalStatistics} from '../models/betting';

@Component({
    selector: 'app-tab-statistics',
    templateUrl: './tab-statistics.component.html',
    styleUrls: ['./tab-statistics.component.scss'],
})
export class TabStatisticsComponent implements OnInit {

    constructor(private bettingService: BettingService) {}

    totMatch: TotalStatistics;
    totDoubling: TotalStatistics;

    ngOnInit() {
        let chart = am4core.create('pieChart3d', am4charts.PieChart3D);
        let chartRaddoppi = am4core.create('pieChart3dRaddoppi', am4charts.PieChart3D);

        this.bettingService.getTotalStatistics()
            .subscribe(rs => {
                this.totMatch = rs;
                chart.data = [
                    { name: 'WIN',  value: rs.win },
                    { name: 'LOSE', value: rs.lose }
                ];
            });

        this.bettingService.getTotalStatisticsRaddoppi()
            .subscribe(rs => {
                this.totDoubling = rs;
                chartRaddoppi.data = [
                    { name: 'WIN',  value: rs.win },
                    { name: 'LOSE', value: rs.lose }
                ];
            });

        // Serie principale
        let pieSeries = chart.series.push(new am4charts.PieSeries3D());
        pieSeries.dataFields.value = 'value';
        pieSeries.dataFields.category = 'name';
        pieSeries.colors.list = [
            am4core.color('#0e5715'),
            am4core.color('#8d0003')
        ];
        pieSeries.hiddenState.transitionDuration = 50000;

        // 🔹 NIENTE TESTO WIN/LOSE SUL GRAFICO
        pieSeries.labels.template.disabled = true;
        pieSeries.ticks.template.disabled = true;

        // Serie raddoppi
        let pieSeriesRaddoppi = chartRaddoppi.series.push(new am4charts.PieSeries3D());
        pieSeriesRaddoppi.dataFields.value = 'value';
        pieSeriesRaddoppi.dataFields.category = 'name';
        pieSeriesRaddoppi.colors.list = [
            am4core.color('#0e5715'),
            am4core.color('#8d0003')
        ];
        pieSeriesRaddoppi.hiddenState.transitionDuration = 50000;

        // 🔹 Anche qui niente etichette
        pieSeriesRaddoppi.labels.template.disabled = true;
        pieSeriesRaddoppi.ticks.template.disabled = true;
    }

}
