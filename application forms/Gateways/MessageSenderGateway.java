import model.loan.LoanRequest;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.io.Serializable;
import java.util.Properties;

public class MessageSenderGateway {

    /*private Connection connection;
    private Session session;
    private Destination destination;
    private MessageProducer msgProducer;

    public MessageSenderGateway(String channelName){

    }

   public Message createTextMessage(String body){
    Message msg
   }





    public static Message request(Serializable message, String channel, Destination destination, MessageListener msgListener) {
        Destination sendDestination;
        MessageConsumer msgConsumer;
        Destination replyDestination;

        try {
            Properties props = new Properties();
            props.setProperty(Context.INITIAL_CONTEXT_FACTORY, "org.apache.activemq.jndi.ActiveMQInitialContextFactory");
            props.setProperty(Context.PROVIDER_URL, "tcp://localhost:61616");

            props.put(("queue." + channel), channel);

            Context jndiContext = new InitialContext(props);
            ActiveMQConnectionFactory connectionFactory = (ActiveMQConnectionFactory) jndiContext.lookup("ConnectionFactory");

            connectionFactory.setTrustAllPackages(true);
            connection = connectionFactory.createConnection();
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            msgProducer = session.createProducer(destination);
            Message msg = session.createObjectMessage(message);

            if (msgListener != null) {
                connection.start();
                replyDestination = session.createTemporaryQueue();
                msg.setJMSReplyTo(replyDestination);
                msgProducer.send(msg);
                msgConsumer = session.createConsumer(replyDestination);
                createListener(msgListener, msgConsumer);
            } else {
                msgProducer.send(msg);
            }
            return msg;
        }catch(JMSException | NamingException e){
            e.printStackTrace();
        }
        return null;
    }

    public static void reply(Serializable message, String channel, Destination destination){
        request(message, channel, destination, null);
    }

    public static Destination createDestination(String channel, String destination){
        Properties props = new Properties();
        props.setProperty(Context.INITIAL_CONTEXT_FACTORY, "org.apache.activemq.jndi.ActiveMQInitialContextFactory");
        props.setProperty(Context.PROVIDER_URL, "tcp://localhost:61616");
        props.put("queue." + channel, channel);

        try{
            Context jndiContext = new InitialContext(props);
            return (Destination) jndiContext.lookup(destination);
        }catch(NamingException e){
            e.printStackTrace();
        }

        return null;
    }*/
}
