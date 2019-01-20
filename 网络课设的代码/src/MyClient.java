import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.StandardChartTheme;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

/**
 * Created by 张金莉
 */
public class MyClient extends ChartPanel implements Runnable {

    /**
     * 本地接收数据包的端口号
     *   本地接收数据包的端口号
     * 	 *接收 UDP 数据包的步骤如下：
     * 	 *使用 DatagramSocket 创建数据包套接字，并将其绑定到指定的端口。
     * 	 *使用 DatagramPacket 创建字节数组来接收数据包。
     * 	 *使用  receive() 方法接收 UDP 包。
     */
    public static final String LOCAL_HOST = "12345";

    private static final long serialVersionUID = 1L;
    //
    private static TimeSeries timeSeries;

    public MyClient(String chartContent, String title, String yAxisName) {
        super(createChart(chartContent, title, yAxisName));
    }

    private static JFreeChart createChart(String chartContent, String title, String yAxisName) {
       ////创建时序图对象
        timeSeries = new TimeSeries(chartContent);
        //创建一个绑定在默认时区数据集
        TimeSeriesCollection timeseriescollection = new TimeSeriesCollection(timeSeries);
        //生产时序图（标题，x轴名称，y轴名称，数据集，是否显示图例，是否生成工具，是否显示url链接）
        JFreeChart jfreechart = ChartFactory.createTimeSeriesChart(title, "时间(秒)", yAxisName,
                timeseriescollection, true, true, false);
        ValueAxis valueaxis = jfreechart.getXYPlot().getDomainAxis();
        //自动设置数据轴时间范围
        valueaxis.setAutoRange(true);
        valueaxis.setFixedAutoRange(30000D);
        return jfreechart;
    }

    @Override
    public void run() {
        DatagramSocket ds = null;
        byte[] bys = null;
        DatagramPacket dp = null;
        double ret = 0;
        while (true) {
            try {
                Thread.sleep(300);
                //创建数据包套接字，并将其绑定到指定的端口。；
                //Integer.parseInt 方法的作用, 就是从字符串转换成一个10进制的整数
                ds = new DatagramSocket(Integer.parseInt(LOCAL_HOST));
                // 创建一个字节数组
                bys = new byte[1024];
                dp = new DatagramPacket(bys, bys.length);

                // 接收数据包
                ds.receive(dp);
                //bys是个字节数组，通过这个字节数组去构建一个String对象
                //通过这个String解析得到一个小数
                //ret就是需要传入的温度值，是个小数
                //Double.parseDouble(String)将字符串转换为双精度
                ret = Double.parseDouble(new String(bys));
            } catch (Exception e) {
                e.printStackTrace();
            }finally {
                timeSeries.add(new Millisecond(), ret);
                ds.close();
            }
        }
    }

    public static void main(String[] args) {
        // 设置显示样式，避免中文乱码
        StandardChartTheme standardChartTheme = new StandardChartTheme("CN");
        standardChartTheme.setExtraLargeFont(new Font("微软雅黑", Font.BOLD, 20));
        standardChartTheme.setRegularFont(new Font("微软雅黑", Font.PLAIN, 15));
        standardChartTheme.setLargeFont(new Font("微软雅黑", Font.PLAIN, 15));
        ChartFactory.setChartTheme(standardChartTheme);
        //创建一个新的窗体，名称为“温度统计图”
        JFrame frame = new JFrame("温度统计图");
        MyClient realTimeChart = new MyClient("温度折线图", "", "温度");
        //向容器中添加组件
        frame.getContentPane().add(realTimeChart, new BorderLayout().CENTER);
        //调整窗口大小
        frame.pack();
        //显示组件
        frame.setVisible(true);
        //初始化温度图的参数以及点击退出按钮的时候结束当前线程
        (new Thread(realTimeChart)).start();
        //在窗口添加一个Windows事件消息，目的是我们关闭窗口的时候可以正常的退出
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent windowevent) {
                System.exit(0);
            }
        });
    }
}
