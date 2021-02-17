import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class OracleFreeSpaceTablespace {

    private static String mailFrom;
    private static String[] mailTo;
    private static String mailPass;

    private static String goodSpace = "";
    private static String warningSpace = "";

    public static void main(String[] args) throws Exception {

        searchFreeSpace(args[0],args[1],args[2],args[3],args[4],args[5]);
        System.out.println(goodSpace);
        System.out.println(warningSpace);

        if (warningSpace.length() > 1){
            mailFrom = args[6];
            mailPass = args[7];
            mailTo = new String[args.length-8];
            for (int i = 0; i < args.length-8; i++){
                mailTo[i] = args[i+8];
            }
        }

        sendMail("Внимание! Заканчиваются TABLECPACE в базе "+ args[2]+"!","Заканчиваются табспейсы!\nРазмер менее "+ args[5]+"М\n" +warningSpace+"\n\nПрочие табспейсы\n"+goodSpace);

    }

    public static void searchFreeSpace(String BaseIp, String basePort, String baseName, String baseUser, String basePass, String spaceWarningLimit) throws Exception {
        Connection oraCon = OracleConnection.initConnect(BaseIp, basePort, baseName, baseUser, basePass);
        try {
            Statement statement = oraCon.createStatement();
            ResultSet cFreeSpace = statement.executeQuery("" +
                    "select t.tablespace_name,\n" +
                    "       t.freeee\n" +
                    "  from udo_ily_free_space_tablespace t" +
                    " order by tablespace_name");

            while (cFreeSpace.next()) {
                //System.out.println(cFreeSpace.getString("tablespace_name") + " - " + cFreeSpace.getString("freeee"));
                if (cFreeSpace.getFloat("freeee") < Integer.parseInt(spaceWarningLimit)){
                    warningSpace += cFreeSpace.getString("tablespace_name") + " - " + cFreeSpace.getString("freeee") + "\n";
                }
                else{
                    goodSpace += cFreeSpace.getString("tablespace_name") + " - " + cFreeSpace.getString("freeee") + "\n";
                }

            }
            cFreeSpace.close();
            oraCon.close();
        }
        catch (Exception ex) {
            //выводим наиболее значимые сообщения
            Logger.getLogger(OracleConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        finally
        {
            if (oraCon != null) {
                try {
                    oraCon.close();
                } catch (SQLException ex) {
                    Logger.getLogger(OracleConnection.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    public static void sendMail (String subject, String msg) throws Exception{

        Mail mail = new Mail(mailFrom, mailPass);//"jqthrnkqngfyfpyw"//"ily-e@yandex.ru"
        mail.setHostPostSport("smtp.yandex.ru","465","465");
        mail.setFrom(mailFrom);//"ily-e@yandex.ru"
        String[] to  = new String[1];;
        to = mailTo;//"ily-e@mail.ru";
        mail.setTo(to);
        mail.setSubject(subject);
        mail.setBody(msg);
        mail.send();

    }




}
