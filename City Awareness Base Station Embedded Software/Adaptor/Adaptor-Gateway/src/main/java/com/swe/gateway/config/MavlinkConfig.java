package com.swe.gateway.config;
import com.swe.gateway.service.UavHandler;
import io.dronefleet.mavlink.Mavlink2Message;
import io.dronefleet.mavlink.MavlinkConnection;
import io.dronefleet.mavlink.MavlinkMessage;
import io.dronefleet.mavlink.common.*;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.math.BigInteger;
import java.net.*;
import java.sql.Date;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.swe.gateway.dao.UavGpsMapper;
import com.swe.gateway.model.UavGps;
import com.swe.gateway.model.UavVfrHud;
import com.swe.gateway.model.UavBatteryStatus;
import io.dronefleet.mavlink.util.EnumValue;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

public class MavlinkConfig extends Thread{

    static byte[] mySecretKey = new byte[1024];
    private static BigInteger timeUsec=null;
    private static float airspeed = 0;
    private static float groundspeed = 0;
    private static int heading = 0;
    private static int throttle = 0;
    private static float altitude = 0;
    private static float climb = 0;
    private static float lon = 0;
    private static float lat = 0;
    private static float alt = 0;
    private static int batteryId = 0;
    private static EnumValue<MavBatteryFunction> batteryFunction;
    private static EnumValue<MavBatteryType> type;
    private static int temperature = 0;
    private static List<Integer> voltages;
    private static int currentBattery = 0;
    private static int currentCconsumed = 0;
    private static int energyCconsumed = 0;
    private static int batteryRemaining = 0;

    private static final Logger logger = LogManager.getLogger(MavlinkConfig.class.getName());
    private String uavId="DX-1";
    UavGpsMapper uavGpsMapper;
    public MavlinkConfig(String uavId,UavGpsMapper uavGpsMapper){
        //this.uavId=uavId;
        this.uavGpsMapper=uavGpsMapper;
    }


    // ?????????????????????VPN?????????
    //static SocketAddress localAddress = new InetSocketAddress("172.16.3.69", 14550);

    static SocketAddress localAddress = new InetSocketAddress("172.16.3.69", 14600);
    //uri=udp://172.26.144.206:5600  or 115200??? 14550


    //???????????????????????????
    static SocketAddress remoteAddress = new InetSocketAddress("172.16.2.88", 14600);
    //static SocketAddress remoteAddress = new InetSocketAddress("172.16.2.102", 14550);

    static int bufferSize = 65535;

    static DatagramSocket datagramSocket;
    static PipedOutputStream appOut;
    static ExecutorService service;
    public void run() {
        try {
            System.out.println("Config:");
            System.out.println(uavId);
            System.out.println(uavGpsMapper);
            getgps();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void getgps(/*String[] args*/) throws IOException {



        datagramSocket = new DatagramSocket(localAddress);

        PipedInputStream udpIn = new PipedInputStream();
        OutputStream udpOut = new OutputStream() {

            final byte[] buffer = new byte[bufferSize];
            int position = 0;
            /*@Autowired
            UavGpsMapper uavGpsMapper;*/

            @Override
            public void write(int i) throws IOException {
                write(new byte[] { (byte)i }, 0, 1);
            }

            @Override
            public synchronized void write(byte[] b, int off, int len) throws IOException {
                // If the buffer is full, we flush
                if ((position + len) > buffer.length) {
                    flush();
                }
                System.arraycopy(b, off, buffer, position, len);
                position += len;
            }

            @Override
            public synchronized void flush() throws IOException {
                DatagramPacket packet = new DatagramPacket(buffer, 0, position, remoteAddress);
                datagramSocket.send(packet);
                position = 0;
                System.out.println("send over");
            }
        };

        //??????MavlinkConnection??????????????????+?????????
        MavlinkConnection connection = MavlinkConnection.create(
                udpIn,
                udpOut
        );

        appOut = new PipedOutputStream(udpIn);

        service = Executors.newSingleThreadExecutor();
        service.execute(new Runnable(){
            public void run() {
                try {
                    DatagramPacket packet = new DatagramPacket(new byte[bufferSize], bufferSize);
                    while (!datagramSocket.isClosed()) {
                        datagramSocket.receive(packet);
                        appOut.write(packet.getData(), packet.getOffset(), packet.getLength());
                        appOut.flush();

                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        appOut.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (!datagramSocket.isClosed()) {
                        datagramSocket.close();
                    }
                    if (!service.isShutdown()) {
                        service.shutdown();
                    }
                }
            }});



        /*
        byte[] receiveBuffer = new byte[bufferSize];
        int readLength;
        while ((readLength = udpIn.read(receiveBuffer)) != -1) {
            udpOut.write(receiveBuffer, 0, readLength);
            udpOut.flush();
        }
        */

        //?????????????????????????????????https://mavlink.io/en/messages/common.html????????????
        int VFR_HUD_ID = 74;
        int BATTERY_STATUS = 147;
        //CommandLong???????????????
        CommandLong requestVFRHUD = CommandLong.builder()
                .command(MavCmd.MAV_CMD_SET_MESSAGE_INTERVAL)
                .param1(BATTERY_STATUS)
                .param2(0)
                .param7(0)
                .targetSystem(1)
                .targetComponent(1)
                .build();
        //VFR_HUD_ID???????????????
        VfrHud fpT =  VfrHud.builder()
                .airspeed(30)
                .groundspeed(33)
                .heading(180)
                .throttle(77)
                .alt(123)
                .climb(5)
                .build();
        //RequestDataStrea?????????????????????????????????MAVLink??????????????????????????????????????????????????????????????????????????????
        RequestDataStream rds = RequestDataStream.builder()
                .targetSystem(0)
                .targetComponent(0)
                .reqStreamId(0)
                .reqMessageRate(2)
                .startStop(1)
                .build();
        //connection.send1(255, 0, rds);
        //udpOut.flush();
//        if ((flag = sendStream_in.read(buffer)) != -1)//?????????????????????
//        {
//            System.out.println("length:"+flag);
//
//            DatagramPacket dp =
//                    new DatagramPacket(buffer, buffer.length, InetAddress.getByName("127.0.0.1"), 9090);//???????????????IP???????????????
//            sendSocket.send(dp);//??????send??????????????????????????????
//            System.out.println("send over");
//            sendStream_out.flush();
//        }

        MavlinkMessage message;
        /*UavGps latestUavGps;
        latestUavGps = new UavGps((long) 13, "DJ-1", 1, 1, 1, Date.from(Instant.now()));
        uavGpsMapper.addUavGps(latestUavGps);*/

        boolean GetInSecond=false;




        while ((message = connection.next()) != null) {

            //String msg = message.toString();
            System.out.println(message);
            //System.out.println(msg);
            if (message instanceof Mavlink2Message) {
                //Mavlink2 message.
                Mavlink2Message message2 = (Mavlink2Message)message;

                if (message2.isSigned()) {
                    //signed message
                    if (message2.validateSignature(mySecretKey)) {
                        // Signature is valid
                    } else {
                        // Signature validation failed
                    }
                } else {
                    //unsigned message.
                }
            } else {
                //Mavlink1 message.
            }

            if (message.getPayload() instanceof Heartbeat) {
                // This is a heartbeat message
                MavlinkMessage<Heartbeat> heartbeatMessage = (MavlinkMessage<Heartbeat>)message;
                String data = heartbeatMessage.toString();
                System.out.println(data);
                System.out.println("received the heartbeat packet.");
            }
            if (message.getPayload() instanceof VfrHud) {
                // This is a heartbeat message
                MavlinkMessage<VfrHud> fp = (MavlinkMessage<VfrHud>) message;
                String data = fp.toString();
                //System.out.println(data);
                //System.out.println("received the VfrHud packet.");
                airspeed = fp.getPayload().airspeed();
                groundspeed = fp.getPayload().groundspeed();
                heading = fp.getPayload().heading();
                throttle = fp.getPayload().throttle();
                altitude = fp.getPayload().alt();
                climb = fp.getPayload().climb();
                /*System.out.println("airspeed:"+airspeed);
                System.out.println("groundspeed:"+groundspeed);
                System.out.println("heading:"+heading);
                System.out.println("throttle:"+throttle);
                System.out.println("altitude:"+altitude);
                System.out.println("climb:"+climb);*/
                UavVfrHud latestUavVfrHud;
                latestUavVfrHud=new UavVfrHud((long)13, uavId, airspeed, groundspeed, heading, throttle, altitude, climb,Date.from(Instant.now()));
                uavGpsMapper.addUavVfrHud(latestUavVfrHud);
                System.out.println("Uav insert a new VfrHud packet.");
            }
            if(message.getPayload() instanceof GpsRawInt) {
                MavlinkMessage<GpsRawInt> gps = (MavlinkMessage<GpsRawInt>) message;
                String data = gps.toString();
                //System.out.println(data);
                //System.out.println("received the gps packet.");
                if (timeUsec==null || timeUsec!= gps.getPayload().timeUsec()) {
                    timeUsec = gps.getPayload().timeUsec();
                    lon = gps.getPayload().lon();
                    lat = gps.getPayload().lat();
                    alt = gps.getPayload().alt();
                    //System.out.println("longitude:" + lon);
                    //System.out.println("latitude:" + lat);
                    //System.out.println("altitude:" + alt);
                    UavGps latestUavGps;
                    latestUavGps = new UavGps((long) 13, uavId, lat, lon, alt, Date.from(Instant.now()));
                    uavGpsMapper.addUavGps(latestUavGps);
                    System.out.println("Uav insert a new gps packet.");
                    GetInSecond=true;
                }
            }
            if(message.getPayload() instanceof BatteryStatus) {
                MavlinkMessage<BatteryStatus> battery = (MavlinkMessage<BatteryStatus>) message;
                String data = battery.toString();
                System.out.println(data);
                //System.out.println("received the Battery Status packet.");
                if (GetInSecond) {
                    //timeUsec = battery.getPayload().timeUsec();
                    batteryId = battery.getPayload().id();
                    batteryFunction = battery.getPayload().batteryFunction();
                    type = battery.getPayload().type();
                    temperature = battery.getPayload().temperature();
                    voltages = battery.getPayload().voltages();
                    currentBattery = battery.getPayload().currentBattery();
                    currentCconsumed = battery.getPayload().currentConsumed();
                    energyCconsumed = battery.getPayload().energyConsumed();
                    batteryRemaining = battery.getPayload().batteryRemaining();
                    /*UavGps latestUavGps;
                    latestUavGps = new UavGps((long) 13, uavId, lat, lon, alt, Date.from(Instant.now()));
                    uavGpsMapper.addUavGps(latestUavGps);
                    System.out.println("insert a new gps packet.");*/
                    UavBatteryStatus uavBatteryStatus;
                    uavBatteryStatus=new UavBatteryStatus((long)13,uavId,batteryId,batteryFunction.value(),type.value(),temperature,/*voltages.get(0)*/0,currentBattery,currentCconsumed,energyCconsumed,batteryRemaining, java.util.Date.from(Instant.now()));
                    uavGpsMapper.addUavBatteryStatus(uavBatteryStatus);
                    System.out.println("Uav insert a new battery packet.");
                    GetInSecond=false;
                }
            }
        }
        System.out.println("U Connection lost.");

    }
}
