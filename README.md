# OpenPortal
支持Huawei H3C Portal V1 V2协议PAP CHAP认证方式的Portal服务器
  OpenPortal认证服务器基础代码、基本功能已经开发完毕，下一步要进行Portal云认证平台开发。
  
  OpenPortalServer开源Portal服务

作者：LeeSon  QQ:25901875  E-Mail:LeeSon@vip.qq.com  OpenPortal官方交流群 119688084

        该软件是基于华为AC/BAS PORTAL协议的服务端程序，Java编写，开源。
最新源代码下载地址：https://github.com/lishuocool  https://git.oschina.net/SoftLeeSon/

新手安装配置说明：

1.首先保证已有JDK1.7环境

2.解压路径无中文及空格

3.配置文件说明   \webapps\ROOT\config.properties

bas_ip=192.168.0.2  //AC设备IP地址
bas_port=2000       //AC设备通信端口  【无需修改】
portal_port=50100   //PORTAL服务监听端口  【无需修改】
sharedSecret=LeeSon //共享密钥
authType=CHAP          //认证类型   【CHAP/PAP】  
timeoutSec=3        //超时 3秒  【无需修改】
portalVer=1         //PORTAL协议版本   【1/2】  

4.配置AC设备 安装和配置Radius服务
  如果使用AC模拟器进行模拟测试则可忽略这部

5.运行 【运行 OpenPortal  服务器】快捷方式  Linux环境的话，都是高手不用我说了

6.浏览器http://服务器IP

6.如果使用AC模拟器测试用户名密码随意  如果真实环境（不用我废话了）

-----------------------------------------------------------------------------
推荐ToughRADIUS服务器  便捷开源好用 具体安装配置见http://forum.toughradius.net


已华为S5700交换机为例，配置信息：

交换机配置如下配置步骤
步骤 1  
创建 VLAN 并配置接口允许通过的 VLAN，保证网络通畅。
# 创建 VLAN10 和 VLAN20。
[SWITCH] vlan batch 10 20 
# 配置交换机连接上行网络的接口 E0/0/1 为 Access 类型接口，并将 GE0/0/1 加入
VLAN20。
[SWITCH] interface Ethernet0/0/1
[SWITCH-Ethernet0/0/2] port link-type access 
[SWITCH-Ethernet0/0/2] port default vlan 20
[SWITCH-Ethernet0/0/2] quit 

# 配置交换机连接 RADIUS 和 portalServer 的接口 E0/0/2 为Access 类型接口，并
将 GE0/0/2 加入 VLAN20。
[SWITCH] interface Ethernet0/0/2 
[SWITCH-Ethernet0/0/2] port link-type access 
[SWITCH-Ethernet0/0/2] port default vlan 20 
[SWITCH-Ethernet0/0/2] quit 

# 创建 VLANIF10 和 VLANIF20，并配置VLANIF 的 IP 地址，以使用户终端、Switch、
企业内网资源之间能够建立起路由。此处假设 VLANIF10 的 IP 地址为192.168.10.20/24；
VLANIF20 的 IP 地址为 192.168.20.29/24。
[SWITCH] interface vlanif 10 
[SWITCH-Vlanif10] ip address 192.168.10.20 24    //10.1

[SWITCH] interface vlanif 20 
[SWITCH-Vlanif20] ip address 192.168.0.1 24    //0.1
[SWITCH-Vlanif20] quit 
步骤 2  
创建并配置 RADIUS 服务器模板、AAA 方案以及认证域。
# 创建并配置 RADIUS 服务器模板“rd1”。
[SWITCH] radius-server template rd1 
[SWITCH-radius-rd1] radius-server authentication 192.168.0.2 1812 
[SWITCH-radius-rd1] radius-server accounting 192.168.0.2 1813 
[SWITCH-radius-rd1] radius-server shared-key simple leeson
[SWITCH-radius-rd1] radius-server retransmit 2 
[SWITCH-radius-rd1] quit 

# 创建 AAA 方案“abc”并配置认证方式为 RADIUS。
[SWITCH] aaa 
[SWITCH-aaa] authentication-scheme abc 
[SWITCH-aaa-authen-abc] authentication-mode radius 
[SWITCH-aaa-authen-abc] quit 
[SWITCH-aaa] accounting-scheme acc 
[SWITCH-aaa-acc-abc] accounting-mode radius 
[SWITCH-aaa-acc-abc] quit 
# 创建认证域“leeson.org”，并在其上绑定AAA 方案“abc”与RADIUS 服务器模板“rd1”。
[SWITCH-aaa] domain leeson.org 
[SWITCH-aaa-domain-isp1] authentication-scheme abc 
[SWITCH-aaa-domain-isp1] accounting-scheme acc 
[SWITCH-aaa-domain-isp1] radius-server rd1 
[SWITCH-aaa-domain-isp1] quit 
[SWITCH-aaa] quit 

# 配置全局默认域为“leeson.org”。用户进行接入认证时，以格式“user@xxx.xxx”输
入用户名即可在xxx.xxx 域下进行 aaa 认证。如果用户名中不携带域名或携带的域名
不存在，用户将会在默认域中进行认证。
[SWITCH] domain leeson.org 

步骤 3  
配置外部 Portal 认证
# 创建并配置名称为“abc”的 Portal 服务器模板。
[SWITCH] web-auth-server abc 
[SWITCH -web-auth-server-abc] server-ip 192.168.0.2 
[SWITCH -web-auth-server-abc] port 50100 
[SWITCH -web-auth-server-abc] shared-key cipher leeson 
[SWITCH -web-auth-server-abc] url http://192.168.0.2
[SWITCH -web-auth-server-abc] quit 

# 使能 Portal 认证功能。
[SWITCH] interface vlanif 10 
[SWITCH -Vlanif10] web-auth-server abc direct
[SWITCH -Vlanif10] quit 

步骤 4  
查看配置的 Portal 服务器的参数信息。
# 执行命令 display web-auth-server configuration 查看 Portal 服务器相关的
配置信息。
<SWITCH>display web-auth-server configuration
   Listening port        : 2000   
Portal                : version 1, version 2  
 Include reply message : enabled  
  ----------------------------------------------------------------------- 
-   Web-auth-server Name : abc    
IP-address           : 192.168.20.30    
Shared-key           : %$%$]$c{$)Bp!XFdN>G2DBG(T#wn%$%$    
Port / PortFlag      : 50100 / NO    
URL                  : http://192.168.0.2  
Bounded Vlanif       : 10   
  -----------------------------------------------------------------------
 -   1 Web authentication server(s) in total 

步骤 5  
在交换机上添加默认路由：ip route-static 0.0.0.0 0.0.0.0 192.168.0.1 
其中 192.168.0.1为交换机上行端口的网关地址

步骤 6  
配置 portal 白名单
portal free-rule 0 destination ip 192.168.0.1 mask 255.255.255.255 
portal free-rule 1 destination ip 192.168.0.2 mask 255.255.255.255 
portal free-rule 2 destination ip xxx.xxx.xxx.xxx mask 255.255.255.255 
其中的 xxx.xxx.xxx.xxx 为用户认证之后上网所需的 DNS 地址，实际以真实网络环境中的 DNS为准
//排除网关 PORTAL服务器 Radius服务器 DNS

可选配置：
接口视图下（vlan-if，ge）：dhcp select relay
# 使能接口VLANIF100的DHCP Relay功能。
[HUAWEI] dhcp enable
[HUAWEI] interface vlanif 100
[HUAWEI-Vlanif100] dhcp select relay

#Portal用户下线探测
portal timer offline-detect命令用来配置Portal认证用户下线探测周期。
undo portal timer offline-detect命令用来恢复下线探测周期的缺省值。
缺省情况下，下线探测周期为300秒。
注意：portal timer offline-detect命令功能仅适用于二层Portal认证方式。
