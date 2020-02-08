package top.kaoshanji.leaning.jdkx.io.books.b001.textFile;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.Scanner;

/**
 * 以文本格式存储对象
 * @author kaoshanji
 * @time 2020/2/8 下午2:08
 */
public class TextFileTest {

    public static void main(String[] args) throws IOException {
        Employee[] staff = new Employee[3];

        // 待保存的数据.
        staff[0] = new Employee("Carl Cracker", 75000, 1987, 12, 15);
        staff[1] = new Employee("Harry Hacker", 50000, 1989, 10, 1);
        staff[2] = new Employee("Tony Tester", 40000, 1990, 3, 15);

        // save all employee records to the file employee.dat
        // 保存一些数据到 employee.dat 文件
        // 只是起始。。设置..保存的文件和编码..
        try (PrintWriter out = new PrintWriter("employee.dat", "UTF-8")) {
            writeData(staff, out);
        }

        // retrieve all records into a new array
        // 从 employee.dat 读取数据
        // 读取出来..只是引导，具体逻辑都交给其他方法
        // 数据来源作为开始..就完成这一步
        try (Scanner in = new Scanner(
                new FileInputStream("employee.dat"), "UTF-8")) {
            Employee[] newStaff = readData(in);

            // print the newly read employee records
            for (Employee e : newStaff)
                System.out.println(e);
        }
    }

    /**
     * 分发每个对象..
     * Writes all employees in an array to a print writer
     * @param employees an array of employees
     * @param out a print writer
     */
    private static void writeData(Employee[] employees, PrintWriter out) throws IOException {
        // write number of employees
        out.println(employees.length);

        for (Employee e : employees)
            writeEmployee(out, e);
    }

    /**
     * 处理读取数据的操作
     * Reads an array of employees from a scanner
     * @param in the scanner
     * @return the array of employees
     */
    private static Employee[] readData(Scanner in) {
        // retrieve the array size
        int n = in.nextInt();
        in.nextLine(); // consume newline

        Employee[] employees = new Employee[n];
        for (int i = 0; i < n; i++)
        {
            employees[i] = readEmployee(in);
        }
        return employees;
    }

    /**
     * 实际存储数据的地方..
     * Writes employee data to a print writer
     * @param out the print writer
     */
    public static void writeEmployee(PrintWriter out, Employee e) {
        out.println(e.getName() + "|" + e.getSalary() + "|" + e.getHireDay());
    }

    /**
     * 构建 Employee 对象
     * 使用读入的每一行数据初始化
     * Reads employee data from a buffered reader
     * @param in the scanner
     */
    public static Employee readEmployee(Scanner in) {
        String line = in.nextLine();
        String[] tokens = line.split("\\|");
        String name = tokens[0];
        double salary = Double.parseDouble(tokens[1]);
        LocalDate hireDate = LocalDate.parse(tokens[2]);
        int year = hireDate.getYear();
        int month = hireDate.getMonthValue();
        int day = hireDate.getDayOfMonth();
        return new Employee(name, salary, year, month, day);
    }

}
