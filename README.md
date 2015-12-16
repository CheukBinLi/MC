请查看分支  V2.10 最终版。以后版本整合到Bin项目里

@Github
https://github.com/fdisk123/MC/tree/v2.10

@Git@OSC
http://git.oschina.net/Z.Ben.L/MC/tree/v2.10

可以用xml写配置。 mc-bean 包里包含MC.dtd(约束)、bean.xml两个文件(例子，可以配合mc-anthing-test/src/main/java/com/ben/mc/AnthingTest/code_test/Xml_Classprocessing.java例子运行)
可以不用xml直接传入描述路径运行即可。
@例子:

		public static void main(String[] args) throws Throwable {
		
            	 //初始化（全局只要运行一次即可）(完成模式，既使用xml，又使用注解)
            	 //new DefaultApplicationContext("bean.xml");
            	 
            	 //或者(完成模式，既使用xml，又使用注解)
      			 //ApplicationContext ac = new DefaultApplicationContext("bean.xml");
      			 
      			 //或者(注解模式，不使用xml)
      			 //new DefaultApplicationContext("test", false, true);
      			 
      			 //或者(注解模式，不使用xml)
			     ApplicationContext ac = new DefaultApplicationContext("test",false, true);
			     
			     //获取实例
			     //IocTest1 i =ac.getBeans("IocTest1");
			     //或者(获取实例)
			     //IocTest1 i = BeanFactory.getBean("IocTest1");
			     //或者(获取实例)
			     IocTest1 i = DefaultApplicationContext.getBean("IocTest1");
			     //运行
		       	 i.aaxx("xxxx");
		       }
		       
注解说明:
@Register  要注入、要注入实现的类使用
      
      @Register
      class a{}
      
@AutoLoad 
      注入后自动装载实现类
      
      @Register
      class a{
        @AutoLoad
        private X x;
      }
      
@Intercept 拦截器(拦截整合类下所有方法/拦截某方法)
拦截器一定要实现com.ben.mc.bean.classprocessing.handler.Interception 接口

      //拦截所有的方法
      @Register
      @Intercept("com.ben.mc.AnthingTest.mc.xml.DefaultInterceptionXml")
      class a{
        @AutoLoad
        private void x(){}
         private void x2(){}
      }
      
      //或者
      //只拦截x() 方法
      @Register
      class a{
        @AutoLoad
        @Intercept("com.ben.mc.AnthingTest.mc.xml.DefaultInterceptionXml")
        private void x(){}
         private void x2(){}
      }

xml配置说明(xml配置顺序必需严格按照 InitSystemClassLoader,Bean,Intercept,ScanToPack 否则报错抛出)
<?xml version="1.0" encoding="UTF-8"?>
<!--指定 约束DTD路径 在mc-bean里有-->
<!-- <!DOCTYPE Context SYSTEM "file:///E:/javaProject/Eclipse/MC/mc-util/src/main/java/Mc.dtd"> -->
<!-- 或者放在同一目录-->
<!DOCTYPE Context SYSTEM "Mc.dtd">
<Context>
	<InitSystemClassLoader value="true" />
	<!-- 注入例子-->
	<Bean name="bi" class="test.B" type="field" ref="xxxxxxxxxxx110" /><!-- test.B里面 bi的字段 要注入xxxxxxxxxxx110名字下的实例(test.BII)
	<Bean name="xxxxxxxxxxx110" class="test.BII" type="class" /><!--自动扫描 test 关键字下的所有包-->注册一个对象 type="class"-->

	<!-- 拦截器 methods="all" 默认 --><!-- 拦截 aaxx3 和 aaxx2 只方法 拦截实现 com.ben.mc.AnthingTest.mc.xml.DefaultInterceptionXml -->
	<Intercept methods="aaxx3,aaxx2" class="com.ben.mc.AnthingTest.mc.xml.XmlIocTest1" name="intercept0083" ref="com.ben.mc.AnthingTest.mc.xml.DefaultInterceptionXml" />

	<ScanToPack value="test,test.**.a" /><!--自动扫描1: test 关键字下的所有包,2 扫描test.**.a 关键字路径下的所有包 -->
</Context>
      
