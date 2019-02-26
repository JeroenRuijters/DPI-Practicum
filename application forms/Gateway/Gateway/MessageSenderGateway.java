package Gateway;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.io.Serializable;
import java.util.Properties;

public class MessageSenderGateway {

    private Connection connection;
    private Session session;
    private Destination destination;
    private MessageProducer msgProducer;
    private String channel;

    public MessageSenderGateway(String channel, Destination destination){
        this.channel = channel;
        this.destination = destination;
    }

   public MessageSenderGateway(String channel, String destinationName){
        this.channel = channel;
        this.destination = createDestination(channel, destinationName);
   }

    public MessageReceiverGateway send(Serializable message) {
        MessageReceiverGateway receiver = new MessageReceiverGateway();

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

            receiver.setConnection(connection);
            receiver.setSession(session);
            receiver.setReceiveDestination(destination);
            msg.setJMSReplyTo(receiver.getReceiveDestination());
            msgProducer.send(msg);

            return receiver;
        } catch(NamingException | JMSException e){
            e.printStackTrace();
        }
        return null;
    }

    public static Destination createDestination(String channel, String destinationName){
        Properties props = new Properties();
        props.setProperty(Context.INITIAL_CONTEXT_FACTORY, "org.apache.activemq.jndi.ActiveMQInitialContextFactory");
        props.setProperty(Context.PROVIDER_URL, "tcp://localhost:61616");
        props.put("queue." + channel, channel);

        try{
            Context jndiContext = new InitialContext(props);
            return (Destination) jndiContext.lookup(destinationName);
        }catch(NamingException e){
            e.printStackTrace();
        }
        return null;
    }
}
