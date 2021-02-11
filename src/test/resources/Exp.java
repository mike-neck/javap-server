import java.util.Random;

class Exp implements AutoCloseable {

    final int config;
    Exp(int config) {
        this.config = config;
    }

    String execute() throws Exception {
        System.out.println("execution : " + config);
        if (config % 2 == 0) throw new Exception("execute : " + config);
        return "execution : " + config;
    }

    @Override public void close() throws Exception {
        System.out.println("close : " + config);
        if (config % 4 == 0) throw new Exception("close : " + config);
    }

    void recover() throws Exception {
        System.out.println("recover : " + config);
        if (config % 8 == 0) throw new Exception("recover : " + config);
    }

    public static void main(String... args) throws Exception {
        var random = new Random();
        var exp = new Exp(random.nextInt(4));
        try (exp) {
            String result = exp.execute();
            System.out.println(result);
        } catch (Exception e) {
            exp.recover();
        }
    }
}

