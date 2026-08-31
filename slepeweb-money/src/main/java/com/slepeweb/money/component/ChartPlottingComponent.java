package com.slepeweb.money.component;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Paint;
import java.awt.geom.Rectangle2D;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.graphics2d.svg.SVGGraphics2D;
import org.springframework.stereotype.Component;

@Component
public class ChartPlottingComponent {

	public SVGGraphics2D plotAssetHistoryAsLine(DefaultCategoryDataset ds) {
		
		JFreeChart chart = ChartFactory.createLineChart(
		         "Asset history", "Years", "Amount (£)", ds,
		         PlotOrientation.VERTICAL, true, true, false);
		
		CategoryPlot plot = chart.getCategoryPlot();
		CategoryAxis domainAxis = plot.getDomainAxis();
		domainAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_90);
		
		CategoryItemRenderer renderer = plot.getRenderer();
		Paint[] colors = new Paint[] {Color.BLUE, Color.RED, Color.YELLOW};
		for (int i = 0; i < colors.length; i++) {
		    renderer.setSeriesStroke(i, new BasicStroke(2.5f));
		    renderer.setSeriesPaint(i, colors[i]);
		}
		
		int width = 1170, height = 600;
		SVGGraphics2D svg2d = new SVGGraphics2D(width, height);
		chart.draw(svg2d,new Rectangle2D.Double(0, 0, width, height));
		
		return svg2d;
	}

	public SVGGraphics2D plotChartAsBarchart(String chartName, DefaultCategoryDataset ds) {
		JFreeChart chart = ChartFactory.createBarChart(
		         chartName, "Years", "Amounts (£)", ds,
		         PlotOrientation.VERTICAL, true, true, false);
		
		CategoryPlot plot = chart.getCategoryPlot();
		CategoryAxis domainAxis = plot.getDomainAxis();
		domainAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_90);
		
		SVGGraphics2D svg2d = new SVGGraphics2D(1000, 600);
	    chart.draw(svg2d,new Rectangle2D.Double(0, 0, 1000, 600));
	    
	    return svg2d;
	}

}
