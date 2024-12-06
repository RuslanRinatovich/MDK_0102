package LR1;

public class Main {
    public static void main (String args[]){

        char[] letters = { 'G', 'A', 'P', 'O', 'U', ' ', 'Z', 'M', 'K'};
        String name = "";
        int[] a = new int[10];
        for (int i = 0; i < letters.length; i++)
        {
            name += letters[i];
            a[i] = i + 1;
            SendMessage(name, a[i]);
        }

    }

    static void SendMessage(String name, int msg)
    {
        System.out.println("Hello, " + name + "! Count to " + msg);
    }

}
