# 收Goods-Aubox(平板端介面)
## (一)專題目的
物流運輸隨著網路及交通的進步，並伴隨著各大電商平台的興起，使現代人越來越仰賴物流的運輸。而我們從日常生活中發現，往往收貨人在接收貨物的時候，可能因去外地出差又或者送貨的時間剛好是在上班、上課的時間等等各種因素，導致收貨者常常無法簽收，同時也讓送貨員白跑一趟，故為了解決此問題，設計了此系統-「Aubox」。
## (二)系統架構及流程
我們打造一個專門收貨用的郵箱且整合了平板將其作為郵箱之操作介面，同時透過ESP8266來控制電子鎖的開關，並為了使用者開發專門的APP。當送貨員抵達時，可透過平板掃描貨品上的QR CODE，此時資料會送到伺服器裡，進行確認，當確認無誤後，郵箱的門會自動開啟，再由送貨員將貨物放入後，關上門即完成送貨。而使用者在APP中可以隨時追蹤貨物運送的目前進度，若使用者較為謹慎或有貴重物品送達時，可選擇開啟視訊模式，當選擇此模式後，在貨物抵達時，使用者可透過平板上的前後鏡頭來即時確認送貨員確實有將貨物放入，並進行線上簽收，用以確保貨物的安全。
![image](https://github.com/WuJammy/my_project_aubox_android/blob/master/structure.png)
<p align="center">圖1: 簡易架構圖</p>

![image](https://github.com/WuJammy/my_project_aubox_android/blob/master/flow.png)
<p align="center">圖2: 簡易流程圖</p>

## (三)郵箱的硬體設備
(1) 郵箱主體 <br>
(2) 平板 <br>
(3) ESP8266 <br>
(4) 繼電器 <br>
(5) 電子鎖 <br>
## (四)平板端介面
### (1) 操作介面
<div align=center> <img  src=https://github.com/WuJammy/my_project_aubox_android/blob/master/image/interface.png/> </div>
<p align="center">主頁面</p>

功能解釋: <br>
(1)掃描條碼: 當貨物送達時，送貨員點擊此可跳至掃描畫面。  <br>
(2)取貨: 使用者要取貨時，點擊可跳轉至取貨畫面(主要用來家中若有多名使用者的便利性及安全性)。<br>
(3)上鎖: 當送貨員將貨物放入後，關上門需點擊上鎖，才會自動鎖上。 <br>

<div align=center> <img  src=https://github.com/WuJammy/my_project_aubox_android/blob/master/image/scan_interface.png/> </div>
<p align="center">掃描頁面</p>

功能解釋: <br>
(1)將QR Code放於鏡頭前，對準畫面的紅線，即可完成掃描。  <br>
(2)左下角的icon，點擊即可返回主頁面。<br>



<div align=center> <img  src=https://github.com/WuJammy/my_project_aubox_android/blob/master/image/take_interface.png/> </div>
<p align="center">取貨頁面</p>

功能解釋: <br>
(1)在輸入處可輸入貨物的編號。<br>
(2)確認: 點擊後，會將輸入處的編號送進資料庫進行比對，若確認該貨物存在於郵箱中，則會自動開啟門讓用戶取貨，反之亦然。<br>
(3)取消: 點擊即可返回主頁面。 <br>


### (2) 開發工具及使用的技術
 [1. Android Studio](https://developer.android.com/studio)  <br> 
 [2. Agora](https://www.agora.io)  <br> 
 [3. Zxing](https://github.com/zxing/zxing)  <br>
 [4. OkHttp](https://square.github.io/okhttp/)  <br>
### (3) 視訊的實現
### (4) 掃描QR Code的實現
### (5) 串接API的實現
