
# 微信支付与安全链接、证书、签名

本仓库是`Java`版本的**微信支付**相关SDK，其中包含了所有微信支付接口的demo。
如果是做支付类型的平台商户，代码有待重构（部分证书信息需要持久化到DB中）。

代码结构图稍后补充。


附：《HTTPS、摘要、加密与签名认证》

# 一、数字摘要

也称为消息摘要，是一个唯一对应一个消息或文本的固定长度的值，由一个单向的Hash函数对消息进行计算而产生，类似指纹。

## 1、消息摘要特点

- 无论输入的消息有多长，计算出来的消息摘要的长度总是固定的。MD5有128位，SHA-1有160比特位。
- 只要输入的消息不同，对其进行摘要以后产生的摘要消息也不同，但相同的输入必会产生相同的输出。
- 并不包含原文的完整信息，只能进行正向的信息摘要，无法找到任何与原信息相关的信息。

## 2、摘要算法

### a) MD5

`MD5`即`Message Digest Algorithm5`，摘要长度为128位。

基于Java的MD5算法：

```java
public static byte[] testMD5(String content) throws Exception {
    MessageDigest md = MessageDigest.getInstance("MD5");
    byte[] bytes = md.digest(content.getBytes("utf8"));
    return bytes;
}
```

### b) SHA

`SHA`即`Secure Hash Algorithm`，安全散列算法。1995年修订版称为`SHA-1`，已成为公认的最安全的散列算法之一。`SHA-1`算法生成的摘要信息长度为160位，运行速度比MD5慢，但也更为安全。

基于Java的SHA-1算法：

```java
public static byte[] testSHA1(String content) throws Exception {
    MessageDigest md = MessageDigest.getInstance("SHA-1");
    byte[] bytes = md.digest(content.getBytes("utf8"));
    return bytes;
}
```

## 3、摘要编码

**计算出摘要转换成字符串，可能会生成一些无法显示和网络传输的控制字符，需要对生成的摘要字符串进行编码，通常包括`十六进制编码`和`Base64编码`。**

### a) 十六进制编码

每4位二进制数据对应一位十六进制数据，十六进制由0~9和A~F来进行表示。**Java中没有无符号整型**，每八位有一位符号位，需要将符号位转换为对应的数值，再转换为对应的十六进制。8位二进制可以转换为2位十六进制，不足2位的进行补0。

### b) Base64编码

Base64是一种基于64个可打印字符来表示二进制数据的方法，由于2的6次方等于64，所以每6位为一个单元，对应某个可打印字符，三个字节有24位，对应于4个Base64单元，即3个字节需要用4个可打印字符来表示。

基于Java的Base64算法：

```java
private static String byte2base64(byte[] bytes) {
    BASE64Encoder base64Encoder = new BASE64Encoder();
    return base64Encoder.encode(bytes);
}

private static byte[] base642byte() throws IOException {
    BASE64Decoder base64Decoder = new BASE64Decoder();
    return base64Decoder.decodeBuffer(base64);
}
```

# 二、加密算法

## 1、对称加密

对称加密算法中的密钥只有一个，发送和接收双方都是用这个密钥对数据进行加密和解密。

对称加密算法特点：**算法公开、计算量小、加密速度快、加密效率高**。

密钥的保护对于加密信息是否安全至关重要。

常用的加密算法：`DES算法`、`3DES算法`和`AES算法`。

### a) DES算法

DES，即`Data Encryptin Standard`，1977年颁布的`数据加密标准`。

明文按64位进行分组，密钥长64位，事实上只有56位参与DES运算。

`3DES`是`DES`向`AES`过度的加密算法，使用3条56位的密钥对数据进行3次加密，是DES的一个安全的变形。

### b) 3DES算法



### c) AES算法



## 2、非对称加密



# 三、数字签名



## 1、数字证书



## 2、X.509



## 3、证书签发



## 4、证书校验



# 四、摘要认证



# 五、签名认证



# 六、HTTPS与SSL

## 1、ssl与tls

## 2、https部署



# 七、OAuth



## 1、授权过程


































