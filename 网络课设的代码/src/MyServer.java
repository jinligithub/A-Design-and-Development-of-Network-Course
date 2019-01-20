import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * Created by 张金莉
 * /**
 *   * 客户服务器端口号
 * 	 *下面是在 Java 中使用 UDP 协议发送数据的步骤。
 * 	 *使用 DatagramSocket() 创建一个数据包套接字。
 * 	 *使用 DatagramPacket() 创建要发送的数据包。
 * 	 *使用 send() 方法发送数据包
 */
public class MyServer {
    /**
     * 客户服务器IP地址
     */
    public static final String CLIENT_ADDRESS = "127.0.0.1";

    /**
     * 客户服务器端口号
     */
    public static final String CLIENT_HOST = "12345";
    public static void main(String[] args) {

        while(true){
            try {
                Thread.sleep(300);
                DatagramSocket ds = new DatagramSocket();
                //生成随机温度
                byte[] bys = (Math.random() * 100+"").getBytes();
                //打包数据
                DatagramPacket dp = new DatagramPacket(bys, bys.length, InetAddress.getByName(CLIENT_ADDRESS), Integer.parseInt(CLIENT_HOST));
                //发送数据
                ds.send(dp);
                ds.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


    }
}
