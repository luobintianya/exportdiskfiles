exportdisktocsv
===============
this tools is used export disk files to a csv file (500G disk within 10-15 minutes). the export file path is "C:\fileDB"
you can import this csv file to mysql or any other db used search the file basic 
information.
used window command as below:
java -jar exportdiskfiles.jar c:\\



the file suffix as below will not export to csv file.
$RECYCLE.BIN $recycle.bin eclipse springsource .png .class 



这个工具用来导出硬盘上所有文件.除开代码里头排除的下列文件.
$RECYCLE.BIN $recycle.bin eclipse  springsource .png .class 

在命令行里执行下列命令即可：

java -jar exportdiskfiles.jar c:\\

C:\\可以换成路径或者具体文件夹
导出文件路径：C:\fileDB
