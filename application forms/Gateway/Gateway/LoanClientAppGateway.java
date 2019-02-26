package Gateway;

import model.loan.LoanReply;
import model.loan.LoanRequest;
import org.apache.activemq.command.ActiveMQObjectMessage;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;

public abstract class LoanClientAppGateway {
    public void applyForLoan(LoanRequest loanRq){
        MessageSenderGateway sender = new MessageSenderGateway("LoanRequest", "LoanRequest");
        MessageReceiverGateway receiver = sender.send(loanRq);

        receiver.awaitReply(new MessageListener() {
            @Override
            public void onMessage(Message message) {
                try{
                    ActiveMQObjectMessage msgObject = (ActiveMQObjectMessage) message;
                    LoanReply LoanRpl = (LoanReply) msgObject.getObject();
                    onLoanReply(loanRq, LoanRpl);
                }catch(JMSException e){
                    e.printStackTrace();
                }
            }
        });
    }

    public void onLoanReply(LoanRequest loanRq, LoanReply loanRpl){

    }
}
