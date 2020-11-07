package pcapreader;

import java.io.*;
import java.util.ArrayList;

public class PcapReader {
    public static BufferedReader bufferedReader;
    public static BufferedInputStream pcapBufferedReader;
    public static void readPcap(File pcap_file, ArrayList<PacketInfo> allPackets) throws IOException{
        System.out.println(pcap_file.getName());
        PacketHeader packet_header = new PacketHeader();
        FileInputStream fis = null;
        try{
            fis = new FileInputStream(pcap_file);
            byte[] pcap_header = new byte[24];
            fis.read(pcap_header);
            int len = 0;
            boolean isheader = true;
            byte[] buf = new byte[16];

            while ((len = fis.read(buf)) != -1) {
                if(isheader == true){
                    packet_header.set_header(buf);
                    int caplen = PacketParse.bytesToInt(packet_header.caplen,0,true);
                    buf = new byte[caplen];
                    isheader = false;
                }else{
                    int frame_len = PacketParse.bytesToInt(packet_header.len,0,true);
                    int second = PacketParse.bytesToInt(packet_header.second,0,true);
                    byte[] ether_header = new byte[14];
                    byte[] ip_header = new byte[20];
                    System.arraycopy(buf,0,ether_header,0,14);
                    if(ether_header[12]==0x08&&ether_header[13]==0x00){
                        System.arraycopy(buf,14,ip_header,0,20);
                        byte[] src_ip_byte = new byte[4];
                        byte[] dst_ip_byte = new byte[4];
                        System.arraycopy(ip_header,12,src_ip_byte,0,4);
                        System.arraycopy(ip_header,16,dst_ip_byte,0,4);
                        String sip = PacketParse.bytesToIp(src_ip_byte);
                        String dip = PacketParse.bytesToIp(dst_ip_byte);
                        int protocol = ip_header[9];
                        if(protocol==6){
                            allPackets.add(new PacketInfo(second, sip, dip));
                        }
                    }

                    isheader = true;
                    buf = new byte[16];
                }
            }
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public static void readCsv(String csv_file, ArrayList<PacketInfo> allPackets) throws IOException{
        BufferedReader bufferedReader = new BufferedReader(new FileReader(csv_file));
        String line;
        while((line = bufferedReader.readLine()) != null){
            long timestamp = Long.parseLong(line.split(",")[0].split("\\.")[0]);
            String sip = line.split(",")[1];
            String dip = line.split(",")[2];

            allPackets.add(new PacketInfo(timestamp, sip, dip));
        }
    }

    public static void setReader(String file) throws IOException{
        bufferedReader = new BufferedReader(new FileReader(file), 65536);
    }

    public static String readNextLine() throws IOException{
        return  bufferedReader.readLine();
    }

    public static void setPcapReader(String pcapFile) throws IOException{
        pcapBufferedReader = new BufferedInputStream(new FileInputStream(pcapFile), 65536);

        byte[] pcap_header = new byte[24];
        pcapBufferedReader.read(pcap_header);
    }

    public static PacketInfo getNextPacket() throws IOException{
        byte[] buff = new byte[16];
        int len = 0;
        PacketHeader packet_header = new PacketHeader();
        PacketInfo packetInfo = null;

        boolean isheader = true;
        while((len = pcapBufferedReader.read(buff)) != -1){
            if(isheader == true){
                packet_header.set_header(buff);
                int caplen = PacketParse.bytesToInt(packet_header.caplen,0,true);
                buff = new byte[caplen];
                isheader = false;
            }else{
                int timestamp = PacketParse.bytesToInt(packet_header.second,0,true);
                byte[] ether_header = new byte[14];
                byte[] ip_header = new byte[20];
                System.arraycopy(buff,0,ether_header,0,14);
                if(ether_header[12]==0x08&&ether_header[13]==0x00){
                    System.arraycopy(buff,14,ip_header,0,20);
                    byte[] src_ip_byte = new byte[4];
                    byte[] dst_ip_byte = new byte[4];
                    System.arraycopy(ip_header,12,src_ip_byte,0,4);
                    System.arraycopy(ip_header,16,dst_ip_byte,0,4);
                    String sip = PacketParse.bytesToIp(src_ip_byte);
                    String dip = PacketParse.bytesToIp(dst_ip_byte);

                    packetInfo = new PacketInfo(timestamp, sip, dip);
                    break;
                }

                isheader = true;
                buff = new byte[16];
            }
        }

        return packetInfo;
    }
}
