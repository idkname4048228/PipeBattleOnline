public class Shop {
    int currentMoney = 20;

    int[] increasePrice = new int[] { 10, 10, 10, 5 };
    int[] pipePrice = new int[] { 10, 10, 15, 20 };
    int[] pipeAmount = new int[] { 0, 0, 0, 0 };
    int[] pipeBuyTimes = new int[] { 0, 0, 0, 0 };
    String[] pipeCode = new String[] { "s", "b", "t", "c" };
    Double increase = (Double) 1.1;

    public int search(String code) {
        for (int i = 0; i < 4; i++) {
            if (code.equals(pipeCode[i])) {
                return i;
            }
        }
        return -1;
    }

    private void checkPrice() {
        for (int i = 0; i < 4; i++) {
            if (pipeBuyTimes[i] == 2) {
                pipeBuyTimes[i] = 0;
                pipePrice[i] *= increase;
            }
        }
    }

    public void buy(String code) {
        int index = search(code);
        if (index < 0)
            return;

        pipeBuyTimes[index]++;
        pipeAmount[index]++;
        currentMoney -= pipePrice[index];
        checkPrice();
    }

    public void use(String code) {
        int index = search(code);
        if (index < 0)
            return;
        pipeAmount[index]--;
    }

    public void gainMoney(int round) {
        Double bonus = (Double) (currentMoney * 0.1);
        currentMoney = (int) ((40 * round) + bonus);
    }

}
