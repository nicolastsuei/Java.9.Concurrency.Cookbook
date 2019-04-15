package concurrency.cookbook.chapter07.recipe07;
/**
 * Using atomic variables
 * @author cuilijian
 *
 */
public class Main {

	public static void main(String[] args) {
		Account account=new Account();
		account.setBalance(1000);

		Company company=new Company(account);
		Thread companyThread=new Thread(company);
		
		Bank bank=new Bank(account);
		Thread bankThread=new Thread(bank);
		
		System.out.printf("Account : Initial Balance: %d\n", account.getBalance());
		
		companyThread.start();
		bankThread.start();
		
		try {
			companyThread.join();
			bankThread.join();
			System.out.printf("Account : Final Balance: %d\n", account.getBalance());
			System.out.printf("Account : Number of Operations: %d\n", account.getOperations());
			System.out.printf("Account : Accumulated commisions: %f\n", account.getCommission());
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
