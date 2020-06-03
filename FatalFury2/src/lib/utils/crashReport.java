package lib.utils;


import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Properties;

// Clase encargada de gestionar las excepciones que provocan el crasheo del juego.
// Cuando el juego crashea, se crea una ventana para que el usuario escriba la posible causa del crasheo,
// una vez escrita la causa, el usuario pulsara aceptar para enviar un mail a una cuenta de correo
// creada unicamente para recivir reportes de crasheo.

/**
 * The type Crash report.
 */
public class crashReport {

    /**
     * The constant from.
     */
    private static String from ="ff2crashreports@gmail.com";
    /**
     * The constant to.
     */
    private static String to ="ff2crashreports@gmail.com";
    /**
     * The constant password.
     */
    private static String password = "FatalFury2";

    /**
     * The constant thread.
     */
    private static String thread;
    /**
     * The constant trace.
     */
    private static String trace;
    /**
     * The constant cause.
     */
    private static JTextField cause;
    /**
     * The constant res.
     */
    private static JLabel res;

    /**
     * The constant activeWindow.
     */
    private static boolean activeWindow = false;

    /**
     * Instantiates a new Crash report.
     */
    public crashReport(){
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            public void uncaughtException(Thread t, Throwable e) {
                if (!activeWindow){
                    thread = t.getName();
                    StringWriter sw = new StringWriter();
                    PrintWriter pw = new PrintWriter(sw);
                    e.printStackTrace(pw);
                    trace = sw.toString();

                    createWindow();
                    activeWindow = true;
                }
            }
        });
    }

    /**
     * Create window.
     */
    private void createWindow(){
        JFrame frame = new JFrame("Crash report");

        frame.add(new Ventana());
        frame.setSize(500, 125);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }


    /**
     * Send mail.
     *
     * @param content the content
     */
    private static void sendMail(String content){
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class",
                "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");
        //get Session
        Session session = Session.getDefaultInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(from,password);
                    }
                });
        //Compose the message
        try {
            MimeMessage message = new MimeMessage(session);
            message.addRecipient(Message.RecipientType.TO,new InternetAddress(to));
            message.setSubject(content);
            message.setText("thread: " + thread +"\ntrace: " + trace);

            //send the message
            Transport.send(message);

            System.out.println("message sent successfully...");

        } catch (MessagingException e) {e.printStackTrace();}
    }


    /**
     * The type Ventana.
     */
    private static class Ventana extends JPanel {
        /**
         * Instantiates a new Ventana.
         */
        private Ventana() {
            setLayout(new BorderLayout());

            JPanel northPanel = new JPanel();
            northPanel.setLayout(new FlowLayout());
            JLabel label = new JLabel("Please explain in detail the cause of the crash.");
            northPanel.add(label);
            add(northPanel, BorderLayout.NORTH);


            JPanel centerPanel = new JPanel();
            cause = new JTextField(40);
            centerPanel.add(cause);
            add(centerPanel, BorderLayout.CENTER);


            JPanel southPanel = new JPanel();
            JButton btn = new JButton("Send report");
            btn.addActionListener(new BtnListener());
            southPanel.add(btn);

            res = new JLabel();
            res.setVisible(false);
            southPanel.add(res);

            add(southPanel, BorderLayout.SOUTH);
        }
    }

    /**
     * The type Btn listener.
     */
    private static class BtnListener implements ActionListener {
        /**
         * Action performed.
         *
         * @param e the e
         */
        @Override
        public void actionPerformed(ActionEvent e) {
            String content = cause.getText();
            crashReport.sendMail(content);
            System.exit(1);
        }
    }
}
