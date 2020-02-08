package top.kaoshanji.leaning.jdkx.io.books.b001.objectStream;

/**
 * 经理
 * @author kaoshanji
 * @time 2020/2/8 下午3:57
 */
public class Manager extends Employee {

    private Employee secretary;

    /**
     * Constructs a Manager without a secretary
     * @param n the employee's name
     * @param s the salary
     * @param year the hire year
     * @param month the hire month
     * @param day the hire day
     */
    public Manager(String n, double s, int year, int month, int day)
    {
        super(n, s, year, month, day);
        secretary = null;
    }

    /**
     * Assigns a secretary to the manager.
     * @param s the secretary
     */
    public void setSecretary(Employee s)
    {
        secretary = s;
    }

    public String toString()
    {
        return super.toString() + "[secretary=" + secretary + "]";
    }

}
