import java.io.*;
import java.util.*;
import static java.lang.Math.pow;

public class project1 {
	static int powc=0;
	public static void main(String[] args) throws FileNotFoundException 
	{
		Scanner input = new Scanner(new File("input.co"));			//takes the input file
		Scanner input1 = new Scanner(new File("input.co"));			//takes the input file
		File f=new File("c:/users/dilruba/output.asm");
		PrintWriter out=new PrintWriter(f);							//writes assembly code into the file
		LinkedList<String> vars=new LinkedList<>();					//stores the variables
		out.println("jmp myprogram");
		String s="";
		//takes the line of the input file and finds the all variables to initialize
		while(input.hasNextLine()){
			String line = input.nextLine();
			line=intopost(line);
			for(int i=0; i<line.length(); i++){
				if(line.charAt(i) == ' ')
				{
					s=line.substring(0, i);
					int size=s.length();
					if(isalpha(s))
					{
						if(!vars.contains(s)){
							for(int j=0; j<size; j++)
							{
								//checks the variable is uppercase or lowercase
								//makes the code case sensitive
								if(s.charAt(j)<91)
									s=s.substring(0, j+1)+"_"+s.substring(j+1);
								vars.add(s);
							}
							if(!line.substring(0, i).equals("pow"))
								out.println(line.substring(0, i)+" dw 0h");
						}



					}
					line = line.substring(i+1);
					i=0;
				}


			}
		}
		LinkedList<String> vars2=new LinkedList<>();				//stores the variables
		int counter=0;												//counts the loops
		out.println("myprogram:");
		//takes the expressions line by line and evaluates 
		while(input1.hasNextLine())
		{
			String line = input1.nextLine();
			boolean iseq=false;
			//checks the parentheses
			if(checkParentheses(line))
			{
				for(int i=0; i<line.length(); i++)
				{
					if(line.charAt(i)=='=')
					{
						iseq=true; }
				}
				if(iseq)
					vars2=opera(intopost(line),out,counter,vars2);
				else { 			//prints the assembly code for printing the result
					System.out.print(line);
					out.println("mov bx,"+line);
					out.println("mov cx,4h");
					out.println("mov ah,02");
					out.println("asloop"+counter+":");
					out.println("mov dx,0fh");
					out.println("rol bx,4h");
					out.println("and dx,bx");
					out.println("cmp dl,0ah");
					out.println("jae digit"+counter);
					out.println("add dl,'0'");
					out.println("jmp print"+counter);
					out.println("digit"+counter+":");
					out.println("add dl,'A'");
					out.println("sub dl,0ah");
					out.println("print"+counter+":");
					out.println("INT 21h");
					out.println("dec cx");
					out.println("jnz asloop"+counter);
					out.println("mov ah,02");
					out.println("mov dl,0ah");
					out.println("INT 21h");

				}
			}
			else
				opera("error",out,counter,vars2);
			counter++;
		}
		out.println("mov ah,4ch");
		out.println("INT 20h");

		out.close();
		input.close();
		input1.close();
	}

	//changes input from infix to postfix
	//takes one parameter as String which contains the line of the text
	//returns a string of changed line
	static String intopost(String line)
	{
		String result = "";					//stores the postfix form of expression
		char myc='!';
		String charordigit="0123456789qertyuiasdfghjklzxcvbnmwoQWERTYUIOPASDFGHJKLZXCVBNM";			//contains all digits and characters
		Stack<String> mystack = new Stack<>();				//stores the operations
		int size=line.length();
		for (int i = 0; i<size; i++)
		{
			String token=line.charAt(i)+"";					//keeps the ith char

			if(token.equals(" "))
			{
				//ignores the first and last space
				if(i==0 || i+1==size)
					token = "";

				else if((charordigit.contains(""+line.charAt(i-1)) || line.charAt(i-1)=='p') && (charordigit.contains(""+line.charAt(i+1)) || line.charAt(i+1)=='p'))
					System.out.println("error"); 

				else if(charordigit.contains(""+line.charAt(i-1)) || line.charAt(i-1)=='p')
					myc=line.charAt(i-1);

				//ignores the space between the operations
				else if(line.charAt(i-1) == '+' || line.charAt(i-1) == '*' || line.charAt(i-1) == '(' || 
						line.charAt(i-1) == ')' || line.charAt(i-1) == ',')
					token = "";

				//ignores the space between the operations
				else if(line.charAt(i+1) == '+' || line.charAt(i+1) == '*' || line.charAt(i+1) == '(' || 
						line.charAt(i+1) == ')' || line.charAt(i+1) == ',')
					token = "";

				//ignores the space between the operations
				else if(i>=3 && i<=size-3 && (line.substring(i-3, i) == "pow" || line.substring(i, i+3) == "pow"))
					token = "";

				//ignores the consecutive spaces
				else if(line.charAt(i+1) == ' ')
				{
					int space = i+1;
					while(space<size && line.charAt(space) == ' ')
					{
						if(line.charAt(space+1) == '+' || line.charAt(space+1) == '*' || line.charAt(space+1) == '(' || 
								line.charAt(space+1) == ')' || line.charAt(space+1) == ',')
							break;
						else if(line.charAt(space-1) == '+' || line.charAt(space-1) == '*' || line.charAt(space-1) == '(' || 
								line.charAt(space-1) == ')' || line.charAt(space-1) == ',')
							break;
						else if(line.charAt(space+1) ==' ')
							space++;

						else
							break;
					}
				}else if(charordigit.contains(myc+"") || myc=='p')
				{
					System.out.println("error");
				}

			}
			else if (token.equals("("))
				mystack.push(token);

			//gives precedence to the operations in the parentheses
			else if (token.equals(")"))
			{
				while (!mystack.isEmpty() && !mystack.peek().equals("("))
					result += mystack.pop()+" ";

				if(!mystack.isEmpty() && mystack.peek().equals("("))
					mystack.pop();

				if(!mystack.isEmpty() && mystack.peek().equals("pow"))
					result += mystack.pop()+" ";

			}
			//checks the comma is whether in valid form or not
			else if (token.equals(","))
			{
				if(i+1==size || i==0)
					System.out.println("error");

				else if(line.charAt(i+1) == '+' || line.charAt(i+1) == '*' || line.charAt(i+1) == ')' ||
						line.charAt(i-1) == '+' || line.charAt(i-1) == '*' || line.charAt(i-1) == '(')
					System.out.println("error");

				else if(line.charAt(i+1) == ' ')
				{
					int space = i+1;
					while(space<size && line.charAt(space) == ' ')
					{
						 //prints error if there is a comma between operations
						if(line.charAt(space+1) == '+' || line.charAt(space+1) == '*' || line.charAt(space+1) == ')' ||
								line.charAt(space-1) == '+' || line.charAt(space-1) == '*' || line.charAt(space-1) == ')')
						{
							System.out.println("error");
							break;
						}
						else if(line.charAt(space+1) == ' ')
							space++;

						else
							break;
					}
				}

				else
				{
					while(!mystack.isEmpty() && !mystack.peek().equals("("))
						result += mystack.pop()+" ";
				}

			}
			//takes the variables
			else if(charordigit.contains(token)){
				while(i<size && charordigit.contains(token)){
					result += token;
					i++;
					if(i<size)
						token=line.charAt(i)+"";
					else break;
				}
				i--;
				result+=" ";
			}
			//takes the operations and push into the stack according to the precedence
			else
			{
				if(token.equals("p"))
				{
					if(i+3<size && line.substring(i, i+3).equals("pow"))
					{
						token="pow";

						while (!mystack.isEmpty() && precedence(token) <= precedence(mystack.peek()))
							result += mystack.pop()+" ";
						mystack.push(token);
						i+=2;
					}
					else
						result+="p";
				}else
				{

					while (!mystack.isEmpty() && precedence(token) <= precedence(mystack.peek()))
						result += mystack.pop()+" ";

					mystack.push(token);
				}

			}

		}

		while (!mystack.isEmpty())
			result += mystack.pop()+" ";
		return result;
	}

	//checks the number and the form of parentheses 
	//takes one parameter as String which contains the expression
	//returns true if the numbers of parentheses are equal and they are in valid form, Otherwise false
	static boolean checkParentheses(String line){

		Stack<String> paranthesis = new Stack<>();
		int size= line.length();
		for(int i = 0; i<size; i++){
			String token = line.charAt(i)+"";			//keeps the ith char
			if(token.equals("("))
				paranthesis.push(token);
			else if(!paranthesis.isEmpty() && token.equals(")"))
				paranthesis.pop();
			else if(paranthesis.isEmpty() && token.equals(")"))
				return false;

		}
		if(!paranthesis.isEmpty())
			return false;

		return true;
	}

	//gives precedence to operands
	//takes one parameter as String
	//returns integer 
	static int precedence(String operant)
	{
		if(operant.equals("+"))
			return 0;
		else if(operant.equals("*"))
			return 1;
		else if(operant.equals("pow"))
			return 2;

		return -1;
	}

	//prints the assembly code for operations
	//takes four parameters
	//String is the postfix expression 
	//PrintWriter prints the code into the output file
	//counter counts the loops
	//LinkedList stores the variables
	static LinkedList<String> opera(String result,PrintWriter out,int counter,LinkedList<String> vars) throws FileNotFoundException
	{

		int equal=0;
		System.out.println(result);
		String s="",s1="";
		//prints error if there is a error in the expression
		if(result.equals("error"))
			out.print("error");
		LinkedList<String> mylist=new LinkedList<>();			//keeps all variables, operations and the value
		for(int i=0; i<result.length(); i++)
		{
			if(result.charAt(i) == ' ')
			{
				mylist.add(result.substring(0, i));
				result = result.substring(i+1);

				i=0;
			}

		}
		s1=mylist.peek();

		equal=mylist.size();
		if(mylist.isEmpty())
			out.println("error");
		int count=0;
		while(!mylist.isEmpty()){
			if(count==(equal-1)){
				out.println("mov bx,offset "+s1);
				out.println("push bx");
			}
			if(mylist.peek().equals("+"))						//prints the assembly code for + operations
			{   
				out.println("pop cx");
				out.println("pop ax");
				out.println("add ax,cx");
				out.println("push ax");
				mylist.removeFirst();


			}else if(mylist.peek().equals("*"))					//prints the assembly code for * operations
			{
				out.println("pop cx");
				out.println("pop ax");
				out.println("mul cx");
				out.println("push ax");
				mylist.removeFirst();

			}else if(mylist.peek().equals("pow"))				//prints the assembly code for pow operations
			{
				if(powc==0)  
				{
					mylist.removeFirst();
					out.println("pop cx");
					out.println("pop ax");
					out.println("ad_power dw 1h");
					out.println("ad_temp dw 0h");
					out.println("begloop"+counter+":");
					out.println("cmp cx,0");
					out.println("je endloop"+counter);
					out.println("push offset ad_temp");
					out.println("pop bp");
					out.println("mov [bp],ax");
					out.println("mul ad_power");
					out.println("push offset ad_power");
					out.println("pop bp");
					out.println("mov [bp],ax");
					out.println("mov ax,ad_temp");
					out.println("mul ax");
					out.println("dec cx");
					out.println("jmp begloop"+counter);
					out.println("endloop"+counter+":");
					out.println("push ad_power");
					powc++;
				}else {

					mylist.removeFirst(); 
					out.println("pop cx");
					out.println("pop ax");
					out.println("mov [ad_power],1");
					out.println("mov [ad_temp],0");
					out.println("begloop"+powc+":");
					out.println("cmp cx,0");
					out.println("je endloop"+powc);
					out.println("push offset ad_temp");
					out.println("pop bp");
					out.println("mov [bp],ax");
					out.println("mul ad_power");
					out.println("push offset ad_power");
					out.println("pop bp");
					out.println("mov [bp],ax");
					out.println("mov ax,ad_temp");
					out.println("mul ax");
					out.println("dec cx");
					out.println("jmp begloop"+powc);
					out.println("endloop"+powc+":");
					out.println("push ad_power");
					powc++;

				}

			}else if(mylist.peek().equals("="))							//prints the assembly code for assignment
			{
				mylist.removeFirst();
				out.println("pop bp");
				out.println("pop ax");
				out.println("mov [bp],ax");



			}
			else if(isalpha(mylist.peek())){
				s=mylist.peek();
				int size=s.length();
				if(!vars.contains(s)){
					for(int i=0; i<size; i++)
					{
						if(s.charAt(i)<91)
							s=s.substring(0, i+1)+"_"+s.substring(i+1);
						vars.add(s);
						System.out.println(s);
					}
				}
				if(count!=0)
					out.println("push "+s);

				mylist.removeFirst();

			}else if(!isalpha(mylist.peek()))
			{
				out.println("push "+mylist.getFirst()+"h");
				mylist.removeFirst();
			}else 
			{
				out.println("error");
				break;
			}
			count++;
		}
		return vars;
	}

	//checks the string and determines its type(variable or value)
	//takes one parameter as String 
	//return true if the String is variable, otherwise false
	static boolean isalpha(String s)
	{
		String alpha="qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM";			//contains all letters
		String digit="0123456789";														//contains all digits
		if(alpha.contains(s.charAt(0)+""))
			return true;
		else if(digit.contains(s.charAt(0)+"")) 
			return false;

		return false;

	}

	//prints the assembly code for printing the result
	//takes two parameters: first is PrintWriter, second is integer
	//PrintWriter is the output file 
	// integer is the counter for loops
	static void print(PrintWriter out,int counter)
	{
		out.println("mov cx,4h");
		out.println("mov ah,02");
		out.println("asloop"+counter+":");
		out.println("mov dx,0fh");
		out.println("rol bx,4h");
		out.println("and dx,bx");
		out.println("cmp dl,0ah");
		out.println("jae digit"+counter);
		out.println("add dl,'0'");
		out.println("jmp print"+counter);
		out.println("digit"+counter+":");
		out.println("add dl,'A'");
		out.println("sub dl,0ah");
		out.println("print"+counter+":");
		out.println("INT 21h");
		out.println("dec cx");
		out.println("jnz asloop"+counter);
		out.println("mov ah,02");
		out.println("mov dl,0ah");
		out.println("INT 21h");
	}

}



