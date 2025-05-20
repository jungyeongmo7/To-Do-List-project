import java.io.IOException;
import java.util.InputMismatchException;
import java.util.Scanner;

public class GradeDist1 {
    static int[] freq = {0,0,0,0,0,0,0,0,0,0};

    public static void main(String[] args) throws IOException{
        int newGrade, index;
        Scanner scan = new Scanner(System.in);

        System.out.print("점수 입력:");

        while(scan.hasNext()){
            try{
                newGrade=scan.nextInt();
                if(newGrade<0)
                throw new NegativeInputException();
                else{
                    index=newGrade/10;
                    freq[index]++;
                }
            } catch(InputMismatchException x){
                System.out.println(x);
                scan.next();
            } catch(NegativeInputException x){
                System.out.println(x);
            }

            System.out.print("점수입력:");
        }
        System.out.println("-------");
        System.out.println("점수분포");
        System.out.println("-------");

        for(int i=0;i<freq.length; i++){
            System.out.println(i*10+"~"+(i*10+9)+":"+freq[i]);
        }
            }
        

    }
    

