package Gateway;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.Properties;

public class MessageReceiverGateway {

    private String channel;
    private String thisDestinationString;

    private Connection connection;
    private Session session;

    private Destination receiveDestination;
    private MessageConsumer msgConsumer = null;

    public MessageReceiverGateway(String channel, String thisDestinationString){
        this.channel = channel;
        this.thisDestinationString = thisDestinationString;
    }

    public MessageReceiverGateway(){ }

    public void startConnection(MessageListener msgListener){

        try {
            Properties props = new Properties();
            props.setProperty(Context.INITIAL_CONTEXT_FACTORY, "org.apache.activemq.jndi.ActiveMQInitialContextFactory");

            props.put(("queue." + channel), channel);

            Context jndiContext = new InitialContext(props);
            ActiveMQConnectionFactory connectionFactory = (ActiveMQConnectionFactory) jndiContext.lookup("ConnectionFactory");

            connectionFactory.setTrustAllPackages(true);
            connection = connectionFactory.createConnection();
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            receiveDestination = (Destination) jndiContext.lookup(thisDestinationString);
            msgConsumer = session.createConsumer(receiveDestination);

            connection.start();

        } catch (NamingException | JMSException e) {
            e.printStackTrace();
        }

        createListener(msgListener, msgConsumer, false);
    }

    private void createListener(MessageListener msgListener, MessageConsumer msgConsumer, boolean isTemp){
        try {
            if (msgListener != null)
                msgConsumer.setMessageListener(new MessageListener() {
                    @Override
                    public void onMessage(Message message) {
                        try{
                            msgListener.onMessage(message);
                            if(isTemp){
                                msgConsumer.close();
                            }
                        }catch(JMSException e){
                            e.printStackTrace();
                        }
                    }
                });
            else {
                msgConsumer.setMessageListener(new MessageListener() {

                    @Override
                    public void onMessage(Message msg) {
                        try {
                            System.out.println("received message: " + msg);
                            if (isTemp) {
                                msgConsumer.close();
                            }
                        }catch(JMSException e){
                            e.printStackTrace();
                        }
                    }
                });
            }

        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    public void awaitReply(MessageListener listener){
        try {
            connection.start();
            msgConsumer = session.createConsumer(receiveDestination);
            createListener(listener, msgConsumer, true);
        }catch(JMSException e){
            e.printStackTrace();
        }
    }

    public String getChannel(){
        return channel;
    }

    public void setChannel(String channel){
        this.channel = channel;
    }

    public String getThisDestinationString() {
        return thisDestinationString;
    }

    public void setThisDestinationString(String thisDestinationString) {
        this.thisDestinationString = thisDestinationString;
    }

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public Destination getReceiveDestination() {
        return receiveDestination;
    }

    public void setReceiveDestination(Destination receiveDestination) {
        this.receiveDestination = receiveDestination;
    }

    public MessageConsumer getConsumer() {
        return msgConsumer;
    }

    public void setConsumer(MessageConsumer consumer) {
        this.msgConsumer = consumer;
    }
}
