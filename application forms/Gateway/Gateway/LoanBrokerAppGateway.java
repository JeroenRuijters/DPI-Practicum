package Gateway;

import model.bank.BankInterestReply;
import model.bank.BankInterestRequest;
import model.loan.LoanReply;
import model.loan.LoanRequest;
import org.apache.activemq.command.ActiveMQObjectMessage;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import java.util.HashMap;

public abstract class LoanBrokerAppGateway {

    private HashMap<String, Destination> Clients = new HashMap<>();

    public void listenForLoanRequests(){
        MessageReceiverGateway receiver = new MessageReceiverGateway("LoanRequest", "LoanRequest");
        receiver.startConnection(new MessageListener() {
            @Override
            public void onMessage(Message message) {
                try {
                    ActiveMQObjectMessage msgObject = (ActiveMQObjectMessage) message;
                    LoanRequest loanRq = (LoanRequest) msgObject.getObject();
                    BankInterestRequest bankIntRq = onLoanRequestReceived(loanRq);
                    requestBankInterest(loanRq, bankIntRq, message.getJMSReplyTo());
                } catch(JMSException e){
                    e.printStackTrace();
                }
            }
        });
    }

    public BankInterestRequest onLoanRequestReceived(LoanRequest loanRq){
        return null;
    }

    public void requestBankInterest(LoanRequest loanRq, BankInterestRequest bankIntRq, Destination destination){
        MessageSenderGateway sender = new MessageSenderGateway("BankInterestRequest", "BankInterestRequest");
        MessageReceiverGateway receiver = sender.send(bankIntRq);

        receiver.awaitReply(new MessageListener() {
            @Override
            public void onMessage(Message message) {
                try{
                    ActiveMQObjectMessage msgObject = (ActiveMQObjectMessage) message;
                    BankInterestReply bankIntRpl = (BankInterestReply) msgObject.getObject();
                    LoanReply loanReply = onBankInterestReply(loanRq, bankIntRpl);
                    replyLoanRequest(loanReply, destination);
                }catch(JMSException e){
                    e.printStackTrace();
                }
            }
        });
    }

    public LoanReply onBankInterestReply(LoanRequest loanRq, BankInterestReply loanRpl){
        return null;
    }

    public void replyLoanRequest(LoanReply loanRpl, Destination destination){
        MessageSenderGateway sender = new MessageSenderGateway("LoanReply", destination);
        sender.send(loanRpl);
    }
}
