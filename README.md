# 项目说明
> 该项目是Andorid智能售货机项目.

## 项目目录

    |-- app_supply  补货APP
    |-- app_vmc     售卖APP
    |-- business    业务目录
    ||-- hotkidclub 俱乐部相关
    ||-- odoo       Odoo相关
    |-- cmd         命令行目录
    ||-- android_back_pressed.sh    模拟Android返回键被按下
    ||-- android_hide_navigation.sh     模拟隐藏导航栏
    ||-- android_show_navigation.sh     模拟显示导航栏
    ||-- code_pull.sh   弃用
    ||-- export_logs.sh     导出日志文件
    |-- core        核心实现
    ||-- liba_core  项目核心
    ||-- liba_log   日志模块
    |-- dependencies    公共依赖
    |-- doc         文档目录
    ||-- yichu      易触相关文档
    |-- liba_project   售卖APP项目工程
    |-- liba_project_supply     补货APP项目工程
    |-- library                 依赖项目
    ||-- liba_core_framework    核心SDK
    ||-- liba_module_location   定位模块
    ||-- liba_module_payment    支付模块
    |-- vmc    VMC模块
    ||-- lib_vmc_core   VMC核心实现
    ||-- liba_vmc_serialport    VMC串口实现
    ||-- liba_vmc_serialport_native VMC Native实现
    ||-- liba_impl_boueki   久保田协议实现
    ||-- liba_impl_develop  调试协议实现
    ||-- liba_impl_yichu    易触协议实现
    |-- gitsync.sh  git submodule同步脚步，每次切换分支之后都应该执行一次该脚本以使代码保持同步


## vmc

售货机适配层. 该目录下的module用于实现售货机的接口定义, 具体售货机的接口实现等.

## business

业务层. 该目录下的module用于处于对应模型的具体业务.

## 构建说明

1. 项目clone

        git clone --recursive http://gitlab.hollywant.com:8181/android/project_vmc.git
    
2. 项目更新
    
        a. git pull --rebase origin develop
        b. git submodule sync
        c. git submodule update

3. 项目提交

    - 主项目改动 && 子项目未改动
    
            git commit(正常提交)
        
    - 主项目未改动 && 子项目改动
    
            a. cd {子项目目录}
            b. git add .
            c. git commit -m 'your commit message'
            
            d. cd {主项目根目录}
            e. git commit(正常提交)
            
    - 主项目改动 && 子项目改动
            
            同上, 但是, 需要先提交子项目改动, 再提交主项目改动, 提交主项目改动时使用`git status`查看文件是否提交完全.

            
## 环境变量说明

Gradle编译时，环境变量由`productFlavors`和`buildTypes`决定，本项目更进一步，引入了`flavorDimensions`进行更为灵活的编译环境变量配置。

### flavorDimensions

* product

    产出的产品类型，有三个值：
    
    - build_debug，开发环境类型的产品。
    - build_test，测试环境类型的产品。
    - build_release，发布环境类型的产品。

* machine
    
    机器类型，当前有三个值，后续会根据适配机型的数量的增加而增加：
    
    - machine_debug， 开发时使用的机型。
    - machine_boueki， 久保田的机型。
    - machine_yichu， 易触的机型。
    
* version

    产品版本，当前有两个值：
    
    - flavors_ruwant，如旺版。
    - flavors_want，旺旺版。
    
### buildTypes
    
构建类型。一般情况下只需两个构建类型`release`和`debug`，为了项目开发中的方便，这里还增加了一个`release_debug`，即可以debug的release类型。

* release

    产出发布类型的安装包，该包会经过`混淆`、`不可debug`、`使用正式签名`等。
    
* debug

    产出调试类型的安装包，该包`不经过混淆`、`可debug`。
    
* release_debug

    产出`使用正式签名`，但`可debug`且`不混淆`源代码的安装包。
    

### 选择合适的环境变量
    
选择环境变量，实际上就是选择合适的`productFlavors`和`buildTypes`组合。
如，我需要在产出一个连接到测试环境、运行在久保田机型上、如旺版本的安装包。根据上文的说明，选择以下productFlavors：

* build_test
* machine_boueki
* flavors_ruwant

即：`build_testMachine_bouekiFlavors_ruwant`。

此外，还需要选择buildTypes。

一般情况可根据实际需求随意选择，如`debug`。

这样一来，最终的环境变量就出来了：`build_testMachine_bouekiFlavors_ruwantDebug`

## 其他

暂略。# smart-vmc
