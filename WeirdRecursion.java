
import java.util.ArrayList;

public class WeirdRecursion {
	static ArrayList<Long> a = new ArrayList<Long>();
	public static void main(String[] args) {
		long startTime = System.currentTimeMillis();
		a.add(1l);
		a.add(1l);
		a.add(3l);
		a.add(1l);
		long sum = 6;
		long max1 = (long)Math.pow(10, 9);
		long max2 = (long)Math.pow(3, 15);
		for(long i = 5; i<=max2;i++){
			a.add(fun1(i));
			sum+=get(i);
			if(sum > max1) sum = sum%max1;
		}
		System.out.println(sum);
		long endTime = System.currentTimeMillis();
		System.out.println((double) (endTime - startTime) / 1000. + "s");
	}
	static long x;
	static long fun1(long n){
		if(n%2 == 0) return get(n/2);
		else if(n%4 == 1){
			x = n/4;
			return 2*get(2*x +1) - get(x);
		}else if(n%4 == 3){
			x = n/4;
			return ((3*get(2*x +1)) - (2*get(x)));
		}
		return 1;
	}
	
	static long get(long index){
		return a.get((int)index-1);
	}
}
