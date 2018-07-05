# IOSDialog
[![License](https://img.shields.io/badge/license-Apache%202-green.svg)](https://www.apache.org/licenses/LICENSE-2.0)
 [ ![Download](https://api.bintray.com/packages/freeleragain/maven/AlibabaOCR/images/download.svg) ](https://bintray.com/freeleragain/maven/AlibabaOCR/_latestVersion)

Android 仿IOS Dialog UI样式 ,通过纯代码实现 ,不用导入额外的图片 ,可定制化能力强

## Screenshot

![](https://github.com/freeler/AlibabaOCR/blob/develop/screenshot/Screenshot_20180705_1.png)
![](https://github.com/freeler/AlibabaOCR/blob/develop/screenshot/Screenshot_20180704_2.png)


## 使用
- 方式 1

```java
compile 'com.freeler:AlibabaOCR:#lastVersion#'
```

- 方式 2. 拷贝Libs工程里面的library到自己的工程里面

## 范例

- 使用Builder方式创建

```java
//初始化一次就好
OcrApi.getInstance().setKey("你的appKey", "你的AppSecret");
//调用Api方法
OcrApi.getInstance().httpTest(file,EnumOcrFace.FACE,callback);
```
callback回调是子线程，如果要在UI线程操作记得转换！！

