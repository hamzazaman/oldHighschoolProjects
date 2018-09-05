package sup;


public class Class {
	public static void main(String[] args) {
		//long startTime = System.currentTimeMillis();
		boolean[] x = new boolean[500];
		findPrimes(x);
		//long endTime = System.currentTimeMillis();
		//System.out.println((double) (endTime - startTime) / 1000. + "s");
		for(int i = x.length - 1; i>=0;i-- ){
			if(x[i] ==true){
				System.out.println(i);
				//break;
			}
		}
			
	}
	
	static void findPrimes(boolean[] x){
		for(int i = 2; i<x.length; i++){
			x[i] = true;
		}
		int l = (int)Math.ceil(Math.sqrt(x.length));
		for(int i = 2; i<l; i++){
			for(int k = 2*i; k<x.length; k+=i){
				x[k] = false;
			}
				
		}
	}
}