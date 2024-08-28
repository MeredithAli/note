import java.time.LocalDate;
import java.util.Objects;

public class Employee{
    private static int nextId;
    private int id;
    private String name;
    private double salary;
    private LocalDate hireDay;
    {
        id = nextId;
        nextId++;
    }

    public Employee(String n, double s){
        name = n;
        salary = s;
    }

    public Employee() {
        name = "";
        salary = 0;
    }

    public Employee(String name, double salary, int year, int month, int day) {
        hireDay = LocalDate.of(year, month, day);
        this.name = name;
        this.salary = salary;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getSalary() {
        return salary;
    }

    public void setSalary(double salary) {
        this.salary = salary;
    }

    public LocalDate getHireDay() {
        return hireDay;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Employee employee = (Employee) o;
        return id == employee.id && Double.compare(salary, employee.salary) == 0 && Objects.equals(name, employee.name) && Objects.equals(hireDay, employee.hireDay);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, salary, hireDay);
    }

    @Override
    public String toString() {
        return "Employee{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", salary=" + salary +
                ", hireDay=" + hireDay +
                '}';
    }
}