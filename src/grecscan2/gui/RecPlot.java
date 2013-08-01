/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package grecscan2.gui;

import grecscan2.core.Recombinant;
import java.awt.Color;
import java.awt.GradientPaint;
import javax.swing.JPanel;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CombinedDomainXYPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYBarPainter;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.DefaultIntervalXYDataset;
import org.jfree.data.xy.IntervalXYDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.RectangleInsets;

/**
 *
 * @author wb
 */
public class RecPlot {
    
    private Recombinant recData;
    private JPanel owner;
    private int index;
    private JFreeChart chart;
    private ChartPanel chartPanel;
    private XYPlot curvePlot;
    private XYPlot boxPlot;
    
    public RecPlot(Recombinant recData){
        this.recData=recData;
        createPlotPanel();
    }
    
    public JPanel getPlotPanel(){
        return this.chartPanel;
    }
    
    private  XYDataset createCurveDataset(){
        XYSeries c1 = new XYSeries("distance");
        double[] xdata=recData.getPos();
        double[] ydata=recData.getPdd();
        for(int i=0;i<xdata.length;i++)
            c1.add(xdata[i],ydata[i]);

        XYSeriesCollection data = new XYSeriesCollection();
	data.addSeries(c1);

	return data;
    }
    
    private  IntervalXYDataset createBoxDataset(){
        DefaultIntervalXYDataset data = new DefaultIntervalXYDataset();
        double[] x,x0,x1,y,y0,y1;
       
        int n=recData.getBlocks().size();
        x=new double[n];
        x0=new double[n];
        x1=new double[n];
        y=new double[n];
        y0=new double[n];
        y1=new double[n];
        int i=0;
        for(int[] b:recData.getBlocks()){
            int start=b[0];
            int end=b[1];
            x[i]=(start+end)/2;
            x0[i]=start;
            x1[i]=end;
            y[i]=0.5;
            y0[i]=0;
            y1[i]=1;
            i++;
        }
        data.addSeries("rec fragments", new double[][]{x,x0,x1,y,y0,y1});
        
        x0=new double[]{1};
        y0=new double[]{0};
        x1=new double[]{recData.getBreakpoints().get(recData.getBreakpoints().size()-1)};
        y1=new double[]{1};
        x=new double[]{(x0[0]+x1[0])/2};
        y=new double[]{0.5};
        data.addSeries("nonrec fragments", new double[][]{x,x0,x1,y,y0,y1});

        return data;
    }
    
    private void createCurvePlot(){
        XYDataset data=createCurveDataset();
            
//        StandardXYItemRenderer renderer = new StandardXYItemRenderer(); 
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        renderer.setBaseShapesVisible(false); //do not display data point
        renderer.setBaseShapesFilled(false);
        renderer.setSeriesPaint(0, new Color(25,127,229));
            
        NumberAxis yaxis = new NumberAxis("distance");
        NumberAxis xaxis= new NumberAxis("site position");
        xaxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
            
        curvePlot = new XYPlot(data, xaxis, yaxis, renderer);
        curvePlot.setDomainCrosshairVisible(true);
        curvePlot.setDomainCrosshairLockedOnData(false);
        curvePlot.setRangePannable(true);
        curvePlot.setRangeZeroBaselineVisible(true);
        curvePlot.setRangeAxisLocation(AxisLocation.BOTTOM_OR_LEFT);
        
//        addBreakPointsMarker(curvePlot);
    }
    
    private void createBoxPlot(){

        
        XYDataset data=createBoxDataset();
            
        NumberAxis yaxis = new NumberAxis();
        yaxis.setVisible(false);
            
        XYBarRenderer renderer=new XYBarRenderer();   
        renderer.setUseYInterval(false);
        renderer.setDrawBarOutline(false);
        renderer.setBarPainter(new StandardXYBarPainter());
        renderer.setShadowVisible(false);
        
        renderer.setSeriesPaint(0, new GradientPaint(0.0F, 0.0F, new Color(25,204,25), 0.0F, 0.0F, new Color(0, 64, 0)));
        renderer.setSeriesPaint(1, new GradientPaint(0.0F, 0.0F, new Color(229,51,25), 0.0F, 0.0F, new Color(0, 0, 64))); 
        
            
        boxPlot = new XYPlot(data, null, yaxis, renderer);
        boxPlot.setDomainGridlinesVisible(false);
        boxPlot.setRangeGridlinesVisible(false);
        boxPlot.setOutlineVisible(false);
        
        
//        XYTextAnnotation xytextannotation = null;
//        Font font = new Font("SansSerif", 0, 9);
//        xytextannotation = new XYTextAnnotation("3rd", -1D, 1D);
//        xytextannotation.setFont(font);
//        xytextannotation.setTextAnchor(TextAnchor.HALF_ASCENT_LEFT);
//        xyplot.addAnnotation(xytextannotation);
        
//        addBreakPointsMarker(boxPlot);
    }
    
    private JFreeChart createChart(){
        createCurvePlot();
        createBoxPlot();
        CombinedDomainXYPlot plot = new CombinedDomainXYPlot(new NumberAxis());
	plot.setGap(10D);
	plot.add(curvePlot, 9);
	plot.add(boxPlot, 1);
	plot.setOrientation(PlotOrientation.VERTICAL);
        plot.setDomainPannable(true);
       
        NumberAxis xaxis=(NumberAxis)plot.getDomainAxis();
        xaxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        xaxis.setRange(1, recData.getBreakpoints().get(recData.getBreakpoints().size()-1));
        
        JFreeChart jfreechart = new JFreeChart(null, JFreeChart.DEFAULT_TITLE_FONT, plot, false);
//        jfreechart.setPadding(new RectangleInsets(10,10,10,20));
                
//          ChartUtilities.applyCurrentTheme(jfreechart);
	return jfreechart;
    }
    
    private void createPlotPanel(){
	chart = createChart();
	chartPanel = new ChartPanel(chart);
	chartPanel.setMouseWheelEnabled(true);
        chartPanel.setRangeZoomable(false);
//        chartPanel.addChartMouseListener(new ChartEventHandler());
    }
}
