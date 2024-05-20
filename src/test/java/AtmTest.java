import com.test.atm.Atm;
import com.test.atm.WithDrawTxn;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class AtmTest {


    @Test
    void withdrawCash_whenSinglePermissibleAmount_thenPermit() {
        Atm atm = new Atm();
        Map<String, List<WithDrawTxn>> response = atm.withdrawCash(500, "Txn1");
        // Deducted ATM cash
        assertEquals(19, atm.getAtmCashMap().get(500));
        // Withdrawn ATM cash
        assertEquals(1, response.get("Txn1").size());
    }

    @Test
    void withdrawCash_whenMultiplePermissibleAmount_thenPermit() {
        Atm atm = new Atm();
        Map<String, List<WithDrawTxn>> response = atm.withdrawCash(700, "Txn1");
        // Deducted ATM cash
        assertEquals(19, atm.getAtmCashMap().get(500));
        assertEquals(29, atm.getAtmCashMap().get(200));
        // Withdrawn ATM cash
        assertEquals(2, response.get("Txn1").size());
    }

    @Test
    void withdrawCash_whenInsufficientAmount_thenDeny() {
        Atm atm = new Atm();
        atm.getAtmCashMap().clear();
        atm.getAtmCashMap().put(500, 1);
        assertThrows(RuntimeException.class, () -> atm.withdrawCash(Integer.MAX_VALUE, "Txn1"));
    }

    @Test
    void withdrawCash_whenAtmBalanceIsZero_thenThrowError() {
        Atm atm = new Atm();
        atm.getAtmCashMap().clear();
        assertThrows(RuntimeException.class, () -> atm.withdrawCash(500, "Txn1"));
    }

    @Test
    void withdrawCash_whenAmountIsZero_thenThrowError() {
        Atm atm = new Atm();
        assertThrows(RuntimeException.class, () -> atm.withdrawCash(0, "Txn1"));
    }

    @Test
    void withdrawCash_whenAmountIsNotMultipleOf10_thenThrowError() {
        Atm atm = new Atm();
        assertThrows(RuntimeException.class, () -> atm.withdrawCash(0, "Txn1"));
    }

    @Test
    void getAtmBalance_whenDefaultBalance_thenReturn18K() {
        Atm atm = new Atm();
        assertEquals(18_000, atm.getAtmBalance());
    }

    @Test
    void getAtmBalance_whenEmptyBalance_thenReturnZero() {
        Atm atm = new Atm();
        atm.getAtmCashMap().clear();
        assertEquals(0, atm.getAtmBalance());
    }

     @Test
    public void test_concurrentTxns() throws InterruptedException, ExecutionException {
         Atm atm = new Atm();
         List<Integer> amounts = Arrays.asList(200,5000,2000,3000);
         ExecutorService executorService = Executors.newFixedThreadPool(amounts.size());
         List<MyCallable> callables = new ArrayList<>();
         for (Integer amount : amounts) {
             callables.add(new MyCallable(amount, "Txn_"+amount, atm));
         }
         List<Future<Map<String, List<WithDrawTxn>>>> futureList = executorService.invokeAll(callables);
         for(Future<Map<String, List<WithDrawTxn>>> future : futureList) {
             future.get();
         }

         assertEquals(7800, atm.getAtmBalance());
         assertEquals(0, atm.getAtmCashMap().get(500));
         assertEquals(29, atm.getAtmCashMap().get(200));
         assertEquals(20, atm.getAtmCashMap().get(100));
     }

     class MyCallable implements Callable<Map<String, List<WithDrawTxn>>> {

         private final int amount;
         private String TxnId;
         private final Atm atm;

        public MyCallable(int amount, String TxnId, Atm atm) {
            this.amount = amount;
            this.TxnId = TxnId;
            this.atm = atm;
        }

        @Override
        public Map<String, List<WithDrawTxn>> call() throws Exception {
            return atm.withdrawCash(amount, TxnId);
        }
    }
}
