package khaja.reopti1;

public class Contact implements Comparable<Contact>{
    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public int getMinutes() {
        return minutes;
    }

    public void setMinutes(int minutes) {
        this.minutes = minutes;
    }

    public int getSeconds() {
        return seconds;
    }

    public void setSeconds(int seconds) {
        this.seconds = seconds;
    }

    String name;
    String number;
    String state;
    String operator;
    int minutes;
    int seconds;

    public Contact(String name, String number, String state, String operator, int minutes, int seconds){
        this.name = name;
        this.number = number;
        this.state = state;
        this.operator = operator;
        this.minutes = minutes;
        this.seconds =  seconds;
    }
    public Contact(){
    }

    @Override
    public int compareTo(Contact contact) {
        return Integer.valueOf(this.seconds).compareTo(contact.getSeconds());
    }
}
