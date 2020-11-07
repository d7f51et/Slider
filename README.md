# Slider
Distributed Denial of Service (DDoS) flooding attacks have been a severe threat to the Internet for decades. These attacks usually are launched by exhausting bandwidth, network resources or server resources. Since most of these attacks are launched abruptly and severely, it is crucial to develop an efficient DDoS flooding attack detection system.

Slider is a novel online sketch-based DDoS flooding attack detection system. Slider utilizes a new type of sketch structure, namely Rotation Sketch, to effectively detect DDoS flooding attacks and efficiently identify the malicious hosts. Meanwhile, Slider also learns the characteristics of the current network during the time specified by the network operator to periodically update the parameters of its detection model.

**This work is being reviewed by ICC 2021.**

# How to start?
## Step 1. Prepare the pcap file
You need to prepare the pcap file that you want to detect.
e.g.`/data/test.pcap`
## Step 2. Start to detect
In `src/main/java/detect/Main.java`, you can use ***Detectcore.slideDetect()*** to start detection.
Here is an example:
```java
//The input pcap file
String pcapFile = "/data/test.pcap";
PcapReader.setPcapReader(pcapFile);

//Detection time interval
detectInterval = 10;

//Damping coefficient
beta=0.7;

//Threshold damping coefficient
lambda=0.4;

//Threshold of slide distance
min_atk_num = 4;

//Training time (second of day)
//e.g. Training at 00:00:00 - 00:10:00
train_time.add(new Pair<Integer, Integer>(0, 600));

//Slider start to train and detect on the input pcap file
DetectCore.slideDetect();
```

# License
This project is licensed under the GPLv3 License.
