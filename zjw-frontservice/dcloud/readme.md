
### 代码开源
开源是因为我目前时间比较少，无法提供与购买插件费用相对应的服务，因此开源。
同样也希望各位一起集思广益，提高保活率
### 购买者的技术支持群
我建了个购买者技术支持群（未购买的用户，想咨询的可以直接加QQ：461882709），群号：824096984<br/>
![avatar](http://testjeawy.oss-cn-beijing.aliyuncs.com/%E5%AE%89%E5%8D%93%E4%BF%9D%E6%B4%BB%E6%8F%92%E4%BB%B6%E6%94%AF%E6%8C%81%E7%BE%A4%E7%BE%A4%E4%BA%8C%E7%BB%B4%E7%A0%81.png)<br/>
建群的目的：为了更好的支持本插件的购买者。<br/>
1 发布新版本之前会在群里发布更新说明，征询购买者的影响范围，尽量对已购买者的影响降到最低。<br/>
2 解决购买者在实际应用过程中，关于插件的问题，更好的为购买者提供插件技术支持。<br/>
### 解决的问题是什么？
答：安卓端，app切换后台、锁屏之后，系统经常会杀死app，导致app定时任务、持续定位等无法正常运行。
### 测试效果如何？ 
答：我们目前在安卓10上测试了我们的APP(持续定位)，手机锁屏或息屏后，路径可以持续定位，最长测试为100公里，如有需要可以联系我（461882709）要测试apk包。<br/>
安卓9中，我们用三星s9+，测试，满足需求。<br/> 
目前测试过的且可以保活的设备有：华为、三星、小米，安卓版本集中在10,9.后续会继续更新我们测试过的设备。<br/> 
华为荣耀系列需设置：设置->应用->应用启动管理,找到自己的应用设置为“手动管理”->"允许后台活动"，其他保活不起作用的设备，可参考设置。
尽量中正式的包名：如com.map2fa.something+自有证书打包
### 关于定时器
我们的插件在绝大部分情况下是可以实现保活需求的，但是不同用户在测试的过程中发现他们的任务被杀死了，很大部分原因是使用了定时器（setInterval），我们在测试过程中发现传统的定时器（setInterval）会被系统杀死，所以我们最新的版本中在原生插件中实现了一个定时器的替代方案，可以实现定时器功能，但是没有使用定时器。使用方法为在启动的时候，传递参数delaysec（毫秒数，即定时功能多久触发一次），传递参数为-1的时候，定时器不会启动，传2000，表明定时器每2秒执行一次，具体参考《使用方法》

#### 定时器的应用场景
1 定时获取位置<br/>
   2 定时刷新日志<br/>
   等等
### 示例工程的使用
1 示例工程上传时需要删除掉APPID，所以如果要运行示例工程的话，需要自己设置下manifest.json->源码视图->appid替换为你们自己的。<br/>
  2 点击右上角的“试用”选择appid对应的工程<br/>
  3 在manifest.json->app原生插件配置->云端插件-选择你刚刚点击试用的插件《安卓保活插件-前台服务插件-白名单-含定时器方案》<br/>
  4 调试：打包自定义基座：运行->运行到手机或模拟器->制作自定义调试基座，<br/>
  5 自定义调试基座打包好后，运行的时候选择基于自定义基座即可运行调试<br/>
### 使用方法： 
1 启动：  

引入插件：
```
// 放在script标签之下，export default之前
const frontservice = uni.requireNativePlugin('zjw-frontservice');
var globalEvent = uni.requireNativePlugin('globalEvent');

// 放在你需要保活的业务逻辑之前
// 比如：点击“开始刷新日志”按钮，将下面frontservice.start的相关代码放在按钮的点击事件代码中。
frontservice.start({ 
                    title: "我正在每5秒刷新一次日志", //小标题
                    big_title: '我正在刷新日志',//大标题
                    content: "请不要关闭该服务，否则我无法刷新日志...",  //详细内容
                    delaysec : 3000,// 毫秒，如果不想启用定时器，传参数 -1
                     
                }, result => { 
                    // 没有返回值，启动成功后，会在通知栏中出现服务。
                });
```
2 停止 

//不需要保活时，调用下面代码

```
const frontservice = uni.requireNativePlugin('zjw-frontservice'); 
frontservice.stop({ //无参数，停止之后服务会停止。 }, result => { 
// 没有返回值，停止之后服务会停止。 });
``` 

3 定时功能回调：
```
//注意这个函数不要重复调用，只调用一次，否则会启动多个监听器，同时监听到定时器的返回数据
globalEvent.addEventListener('position', function(e) {
    // 定时器回调，每隔delaysec毫秒之后，该函数会被调用一次，当delaysec为-1时，该函数不会被调用
    // 执行你的定时逻辑
    ……
    // 执行你的定时逻辑结束
});
```
### 常见问题
#### 1 前台服务没有起来（在通知栏看不到)  
    检查下是不是没有打开通知 
