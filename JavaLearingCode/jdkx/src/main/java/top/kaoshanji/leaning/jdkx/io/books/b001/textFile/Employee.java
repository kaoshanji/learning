package top.kaoshanji.leaning.jdkx.io.books.b001.textFile;

import java.time.LocalDate;

/**
 * 实体 bean
 * @author kaoshanji
 * @time 2020/2/8 下午2:07
 */
public class Employee {

    private String name;
    private double salary;
    private LocalDate hireDay;

    public Employee(String n, double s, int year, int month, int day)
    {
        name = n;
        salary = s;
        hireDay = LocalDate.of(year, month, day);
    }

    public String getName()
    {
        return name;
    }

    public double getSalary()
    {
        return salary;
    }

    public LocalDate getHireDay()
    {
        return hireDay;
    }

    public void raiseSalary(double byPercent)
    {
        double raise = salary * byPercent / 100;
        salary += raise;
    }

    public String toString()
    {
        return getClass().getName() + "[name=" + name + ",salary=" + salary + ",hireDay=" + hireDay + "]";
    }

}
