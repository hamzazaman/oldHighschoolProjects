public class pattern{
	public static void main(String[] args){
		
		while(true){
			try {
				Thread.sleep(2); 
			} catch(InterruptedException ex) {
				Thread.currentThread().interrupt();
			}
				if(Math.random()>0.5)
					System.out.print("/");
				else
					System.out.print("\\");
		}
	}
}