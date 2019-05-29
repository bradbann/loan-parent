## online deploy plan

#### 应用服务器
    172.19.197.248 conf.loan.abc 
##### loan-main 47.100.254.138(172.19.197.248) 
    Midware  : 
    Base App :  gateway-api,eureka-server,config-server
    Busi App : loan-admin,loan-schedule,loan-sms,loan-user
    
##### loan-risk  106.14.213.191(172.19.197.249)
    Midware  :  zookeeper,activemq
    Base App :  
    Busi App :  loan-risk-core,loan-risk-data,loan-statistic


####
    mvn clean package clean package -Dmaven.test.skip=true



#### 启动命令
    ./zbuild/build.sh eureka-server -prod 
    ./zbuild/build.sh config-server eurekaHost=conf.loan.abc repos=loan -prod
    ./zbuild/build.sh gateway-api eurekaHost=conf.loan.abc -prod
    ./zbuild/build.sh admin-server eurekaHost=conf.loan.abc -prod
    
    ./zbuild/compile_deploy.sh loan-admin -prod
    ./zbuild/compile_deploy.sh loan-schedule -prod
    ./zbuild/compile_deploy.sh loan-sms -prod
    ./zbuild/compile_deploy.sh loan-user -prod
    
    ./zbuild/compile_deploy.sh loan-risk-core -prod
    ./zbuild/compile_deploy.sh loan-risk-data -prod
    ./zbuild/compile_deploy.sh loan-statistic -prod