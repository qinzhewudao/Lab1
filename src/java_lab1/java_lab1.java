/* 1: 支持减号和空格
 * 2：支持多个字母之间，字母括号之间省略乘号
 * 3：支持浮点数
 * 4：支持命令语法检查
 * 5：支持同时赋值多个变量
 * 6：支持浮点数的幂，支持无穷多括号嵌套后的幂，支持高次（十位数以上）整数幂
 * 7: 支持表达式基本语法检查
 * */


package java_lab1;


import java.util.regex.*;

public class java_lab1 
{
	final static double EPS=1e-6;//判断某double是否为0用
	final static int MAX_NUMEBR = 1000;//最大支持项数
	final static int BIG_NUMEBR = 30;//变量数
	final static int CHECK_NUMEBR = 30;//变量数	
	static int exponent_matrix[][];//存储次方数据
	static double coefficient_array[];//存储系数

	public static boolean zero(double a)
	{
		return (a<EPS)&&(a>-EPS);
	}
	
	public static boolean syntax_check(String a)//表达式基本语法检查
	{
		if(!a.matches("^[0-9a-zA-Z().+*\\-\\^]+$"))//基本字符检查
		{
			return false;
		}
		
		Pattern[] check =new Pattern[CHECK_NUMEBR];
		int check_index=0;
		check[check_index++]=Pattern.compile("^.*\\.[^0-9].*$");//小数点后面跟的不是数字的情况
		check[check_index++]=Pattern.compile("^.*\\^[0-9]*\\.[0-9]*$");//小数点前面不是数字的情况
		
		check[check_index++]=Pattern.compile("^.*\\^[0-9]*[.\\^].*$");//^后小数的情况
		check[check_index++]=Pattern.compile("^.*\\^[^0-9].*$");//^后不是数字的情况
		
		for(int i=0;i<check_index;i++)
		{
			Matcher test=check[i].matcher(a);
			if(test.find())
			{
				return false;
			}
		}

		//括号的检查
		int left_parentheses=0;
		int right_parentheses=0;
		for(int i=0;i<a.length();i++)
		{
			if(a.charAt(i)=='(')
			{
				left_parentheses++;
			}
			if(a.charAt(i)==')')
			{
				right_parentheses++;
			}
			if(left_parentheses<right_parentheses)
			{
				return false;
			}
		}
		if(left_parentheses!=right_parentheses)
		{
			return false;
		}//对括号的判别
		
		return true;
	}
	
	public static String initialize(String a)//对字符串进行预处理
	{
		
		a=a.replaceAll("[\\s]","");//先处理空格

		a=a.replaceAll("([^0-9])\\.", "$10.");//小数点前面的0的补充
		
		a=a.replaceAll("([a-zA-Z0-9])([A-Za-z])", "$1*$2");//字母之间的乘号
		a=a.replaceAll("([a-zA-Z0-9])([A-Za-z])", "$1*$2");//由于正则表达式的特性，此处要两次
		
		a=a.replaceAll("([a-zA-Z])([0-9])","$1*$2" );//a4将补充为a*4
		
		a=a.replaceAll("([0-9a-zA-Z])(\\()", "$1*$2");//数字/字母乘括号

		a=a.replaceAll("(\\))([A-Za-z0-9])", "$1*$2");//括号乘字母/数字
		//注意上面一定要选择非贪心模式
		a=a.replaceAll("\\)\\(",")*(");//括号乘括号
	    System.out.println(a);		
		a=a.replace("-","+%*");//用%代表-1,将其视为一个新的数字
		
		while(a.contains("(")||a.contains("^"))
		{
			String old_string="";
			old_string=old_string.concat(a);
			StringBuffer buffer1=new StringBuffer();
			Pattern regex_test1=Pattern.compile("(\\([^()]+\\)|[0-9.]+|[a-zA-Z]+)\\^([.0-9]+)"); 
			Matcher regex_test1_matcher=regex_test1.matcher(a);
			while(regex_test1_matcher.find())
			{
				String to_insert=regex_test1_matcher.group(1);
				for(int i=1;i<Integer.parseInt(regex_test1_matcher.group(2));i++)
				{
					to_insert+="*"+regex_test1_matcher.group(1);				
				}
				regex_test1_matcher.appendReplacement(buffer1, to_insert);
			}
			regex_test1_matcher.appendTail(buffer1);
			a=buffer1.toString();//以上为幂的处理	
		    
		//	System.out.println(a);	
		    
		    
	    	a=a.replaceAll("([%0-9.a-zA-Z]+)\\*\\(([%0-9.a-zA-Z*]+)\\+([^()]*)\\)","($1*$2+$1*($3))");
			a=a.replaceAll("\\(([%0-9.a-zA-Z*]+)\\+([^()]*)\\)\\*([%0-9.a-zA-Z]+)","($1*$3+($2)*$3)");

		    a=a.replaceAll("\\(([%0-9.a-zA-Z*]+?)\\+([^()]*)\\)\\*\\(([%0-9.a-zA-Z*]+?)\\+([^()]*)\\)","($1*$3+$3*($2)+$1*($4)+($2)*($4))");
			//去乘号,加括号
		    		    
		    a=a.replaceAll("\\(([.0-9a-zA-Z%*]*)\\)","$1");
		    a=a.replaceAll("\\(\\(([^()]+)\\)\\+", "($1+");
		    a=a.replaceAll("\\+\\(([^()]+)\\)\\)", "+$1)");
		    a=a.replaceAll("\\+\\(([^()]+)\\)\\+", "+$1+");
	    	a=a.replaceAll("^\\(([^()]+)\\)\\+","$1+");
	    	a=a.replaceAll("\\+\\(([^()]+)\\)$","+$1");
	    	// 去括号
	    	a=a.replaceAll("^\\(([^()]+)\\)$","$1");
	    	// 去括号形如  （12+3432*67）
	    	
	    	if(a.equals(old_string))
	    	{
	    		System.out.println("Can not simpify !");
	    		System.out.println("Check syntax !");
	    		return "";
	    	}
		}
		
		return a;
	}
	    
	public static void string_to_array(String a)//把处理过的字符串存储在矩阵中
	{
	//	System.out.println(a);
		
		exponent_matrix =new int[MAX_NUMEBR][BIG_NUMEBR];
		coefficient_array = new double[MAX_NUMEBR];
		String string_matrix[]=a.split("\\+");		

		
		for(int i=0;i<MAX_NUMEBR;i++)
		{
			coefficient_array[i]=0;
		}
		
		for(int i=0;i<string_matrix.length;i++ )
		{
			coefficient_array[i]=1;
			
			StringBuffer buffer1=new StringBuffer();
			Pattern regex_test1=Pattern.compile("([.0-9]+)");
			Matcher regex_test1_matcher=regex_test1.matcher(string_matrix[i]);
			while(regex_test1_matcher.find())
			{
				coefficient_array[i]*=Double.parseDouble(regex_test1_matcher.group(0));
				String to_insert="";
				regex_test1_matcher.appendReplacement(buffer1, to_insert);
			}
			regex_test1_matcher.appendTail(buffer1);
			string_matrix[i]=buffer1.toString();			
			
			int coefficient_check_number=0;
			for(int j=0;j<string_matrix[i].length();j++)
			{//找%出现的次数
				if(string_matrix[i].charAt(j)=='%')
				{
					coefficient_check_number++;
				}
			}
			if(coefficient_check_number%2==1)
			{
				coefficient_array[i]*=-1;
			}//处理符号	
			string_matrix[i]=string_matrix[i].replaceAll("%", "");//剔除%	
			string_matrix[i]=string_matrix[i].replaceAll("\\*", "");//剔除*	
			//以上为对2.34、%之类数据的处理，处理完之后每个字符串都是类似you之类的
			
			
			//System.out.println(string_matrix[i]);
			//System.out.println(string_matrix[i].length());
			//System.out.println("=================================");
			for(int j=0;j<string_matrix[i].length();j++)
			{
				exponent_matrix[i][(int)(string_matrix[i].charAt(j)-'a')]++;
			}
		}
		//次幂存储在数组中 
		
		//以下进行合并同类项和化简
		for(int i=0;i<MAX_NUMEBR;i++)
		{
			if(zero(coefficient_array[i]))
			{
				continue;
			}
			for(int j=i+1;j<MAX_NUMEBR;j++)
			{
				if(zero(coefficient_array[j]))
				{
					continue;
				}//系数为零直接跳过
				
				int check=1;
				for(int k=0;k<BIG_NUMEBR;k++)
				{
					if(exponent_matrix[i][k]!=exponent_matrix[j][k])
					{
						check=0;
						break;
					}
				}
				if(check==1)
				{
					coefficient_array[i]+=coefficient_array[j];
					coefficient_array[j]=0;
				}
			}
		}
	}
	
	public static String matrix_to_string(int a[][],double b[])//把矩阵存储转化为字符串
	{
		for(int i=0;i<MAX_NUMEBR;i++)
		{
			if(zero(b[i]))
			{
				continue;
			}
			for(int j=i+1;j<MAX_NUMEBR;j++)
			{
				if(zero(b[j]))
				{
					continue;
				}//系数为零直接跳过
				
				int check=1;
				for(int k=0;k<BIG_NUMEBR;k++)
				{
					if(a[i][k]!=a[j][k])
					{
						check=0;
						break;
					}
				}
				if(check==1)
				{
					b[i]+=b[j];
					b[j]=0;
				}
			}
		}
		//合并同类项
		
		String to_return ="";
		for(int i=0;i<MAX_NUMEBR;i++)
		{
			if(!zero(b[i]))	
			{	
				if(b[i]>0)
				{
					to_return=to_return.concat("+"+Double.toString(b[i]));
					for(int j=0;j<BIG_NUMEBR;j++)
					{
						for(int k=0;k<a[i][j];k++)
						{
							char temp=(char) (j+'a');
							to_return=to_return.concat("*"+temp);
						}
					}
				}
				else
				{
					to_return=to_return.concat(Double.toString(b[i]));
					for(int j=0;j<BIG_NUMEBR;j++)
					{
						for(int k=0;k<a[i][j];k++)
						{
							char temp=(char) (j+'a');
							to_return=to_return.concat("*"+temp);
						}
					}					
				}
			}
		}
		
		to_return=to_return.replaceAll("^\\+(.*)$", "$1");	//去掉表达式开头的加号	
		
		
		to_return=to_return.replaceAll("(\\.[0-9]*[1-9]+)0{5,}[1-9]{1,}","$1");//解决java本身储存精度的问题
		//某个小数点后出现很多次0，末尾一个非零，直接舍去结尾的非零。
		if(to_return.isEmpty())
		{
			to_return="0";
		}
		return to_return;
	}
	
	public static String simplify(String command)//矩阵中进行化简运算
	{
		int matrix_calculate[][]=new int[MAX_NUMEBR][BIG_NUMEBR];
		double coefficient_calculate[]=new double[MAX_NUMEBR];
		for(int i=0;i<MAX_NUMEBR;i++)
		{
			coefficient_calculate[i]=coefficient_array[i];
		}
		for(int i=0;i<MAX_NUMEBR;i++)
		{
			for(int j=0;j<BIG_NUMEBR;j++)
			{
				matrix_calculate[i][j]=exponent_matrix[i][j];
			}
		}
		
		command=command.replaceAll("^!simplify[\\s]*","");
		while (command.contains("  "))
		{
			command=command.replaceAll("  ", " ");
		}
		String command_list[]=command.split(" ");
		for(int i=0;i<command_list.length;i++)
		{
			int index=command_list[i].charAt(0)-'a';
			double value=Double.parseDouble(command_list[i].substring(2));
			for(int j=0;j<MAX_NUMEBR;j++)
			{
				if(!zero(coefficient_calculate[j])&&matrix_calculate[j][index]!=0)
				{
					coefficient_calculate[j]*=Math.pow(value, matrix_calculate[j][index]);
				}
				matrix_calculate[j][index]=0;
			}
		}
		return matrix_to_string(matrix_calculate, coefficient_calculate);			
	}
	
	public static String deriative(String command)//矩阵中进行求导运算
	{
		int index=command.charAt(4)-'a';
		
		int matrix_calculate[][]=new int[MAX_NUMEBR][BIG_NUMEBR];
		double coefficient_calculate[]=new double[MAX_NUMEBR];
		for(int i=0;i<MAX_NUMEBR;i++)
		{
			coefficient_calculate[i]=coefficient_array[i];
		}
		for(int i=0;i<MAX_NUMEBR;i++)
		{
			for(int j=0;j<BIG_NUMEBR;j++)
			{
				matrix_calculate[i][j]=exponent_matrix[i][j];
			}
		}

		for(int i=0;i<MAX_NUMEBR;i++)
		{
			coefficient_calculate[i]*=matrix_calculate[i][index];
			if(matrix_calculate[i][index]!=0)
			{
				matrix_calculate[i][index]--;
			}
		}
		
		return matrix_to_string(matrix_calculate, coefficient_calculate);
	}
		
	public static void main(String args[])
	{
		String a="a+b+c";//之前导致bug的原因是100这个MAX_NUMEBR设置的太小了

		//a=myinput.read_string();
		//a="(1+2)2";
		//!simplify a=1.001 b=1.5 c=1.333 d=0 f=1 g=2 h=10
		//!simplify a=1.001 b=1.5 c=1.333   g=2 h=10//这个会出现误差，不要拿这个颜演示 
		while(!syntax_check(a))
		{
			System.out.println("Syntax error! Input again");
			a=myinput.read_string();			
		}
		a=initialize(a);
		System.out.println(a);
		string_to_array(a);
		
		System.out.println("Simplified expression:");
		System.out.println(matrix_to_string(exponent_matrix, coefficient_array));
		System.out.println("Input command:");		
		String command="";
		
		while(true)
		{
			command=myinput.read_string();
			if(command.matches("^!simplify[\\s]+([a-zA-Z]=[-0-9.]+[\\s]*)+$"))
			{
				System.out.println("Simplify:");	
				System.out.println(simplify(command));
			}
			else if(command.matches("^!d/d[a-zA-Z]$"))
			{
				System.out.println("Deriative:");
				System.out.println(deriative(command));
			}
			else if(command.matches("^![\\s]*d\\/d[\\s]*$"))
			{
				System.out.println("No variable .");				
			}
			else if(command.contains("quit")||command.contains("exit"))
			{
				System.out.println("Over.");				
				break;
			}
			else if(command.matches("^!simplify[\\s]*$"))
			{
				System.out.println(matrix_to_string(exponent_matrix, coefficient_array));
			}
			else
			{
		    	System.out.println("Input error !");			
			}			
		}
	}
}
