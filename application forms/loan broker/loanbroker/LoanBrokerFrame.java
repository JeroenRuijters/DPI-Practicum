package loanbroker;

import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

import messaging.requestreply.JMSCommunicator;
import model.bank.*;
import model.loan.LoanReply;
import model.loan.LoanRequest;
import org.apache.activemq.command.ActiveMQObjectMessage;


public class LoanBrokerFrame extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private DefaultListModel<JListLine> listModel = new DefaultListModel<JListLine>();
	private JList<JListLine> list;
	private static boolean isConnected = false;
	
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					LoanBrokerFrame frame = new LoanBrokerFrame();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}


	/**
	 * Create the frame.
	 */
	public LoanBrokerFrame() {
		if (!isConnected){
			setupConnection();
		}
		setTitle("Loan Broker");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		GridBagLayout gbl_contentPane = new GridBagLayout();
		gbl_contentPane.columnWidths = new int[]{46, 31, 86, 30, 89, 0};
		gbl_contentPane.rowHeights = new int[]{233, 23, 0};
		gbl_contentPane.columnWeights = new double[]{1.0, 0.0, 1.0, 0.0, 0.0, Double.MIN_VALUE};
		gbl_contentPane.rowWeights = new double[]{1.0, 0.0, Double.MIN_VALUE};
		contentPane.setLayout(gbl_contentPane);
		
		JScrollPane scrollPane = new JScrollPane();
		GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		gbc_scrollPane.gridwidth = 7;
		gbc_scrollPane.insets = new Insets(0, 0, 5, 5);
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		gbc_scrollPane.gridx = 0;
		gbc_scrollPane.gridy = 0;
		contentPane.add(scrollPane, gbc_scrollPane);
		
		list = new JList<JListLine>(listModel);
		scrollPane.setViewportView(list);
	}
	
	 private JListLine getRequestReply(LoanRequest request){    
	     
	     for (int i = 0; i < listModel.getSize(); i++){
	    	 JListLine rr =listModel.get(i);
	    	 if (rr.getLoanRequest() == request){
	    		 return rr;
	    	 }
	     }
	     
	     return null;
	   }
	
	public void add(LoanRequest loanRequest){		
		listModel.addElement(new JListLine(loanRequest));		
	}
	

	public void add(LoanRequest loanRequest,BankInterestRequest bankRequest){
		JListLine rr = getRequestReply(loanRequest);
		if (rr!= null && bankRequest != null){
			rr.setBankRequest(bankRequest);
            list.repaint();
		}		
	}
	
	public void add(LoanRequest loanRequest, BankInterestReply bankReply){
		JListLine rr = getRequestReply(loanRequest);
		if (rr!= null && bankReply != null){
			rr.setBankReply(bankReply);
            list.repaint();
		}		
	}

	private void setupConnection(){
		isConnected = true;
		JMSCommunicator.setupReceiver("LoanRequest", "LoanRequest", new MessageListener() {
			@Override
			public void onMessage(Message message) {
				try {
					ActiveMQObjectMessage msgObject = (ActiveMQObjectMessage) message;
					LoanRequest loanRqst = (LoanRequest) msgObject.getObject();
					Destination replyDestination = msgObject.getJMSReplyTo();
					add(loanRqst);
					BankInterestRequest bankInterestRqst = new BankInterestRequest(loanRqst.getAmount(), loanRqst.getTime());
					add(loanRqst, bankInterestRqst);
					Destination rqstDestination = JMSCommunicator.createDestination("BankInterestRequest", "BankInterestRequest");
					JMSCommunicator.request(bankInterestRqst, "BankInterestRequest", rqstDestination, new MessageListener() {
						@Override
						public void onMessage(Message message) {
							try {
								ActiveMQObjectMessage msgObject = (ActiveMQObjectMessage) message;
								BankInterestReply bankInterestRpl = (BankInterestReply) msgObject.getObject();
								LoanReply loanRply = new LoanReply(bankInterestRpl.getInterest(), bankInterestRpl.getQuoteId());
								add(loanRqst, bankInterestRpl);
								JMSCommunicator.reply(loanRply, "LoanReply", replyDestination);
							} catch (JMSException e) {
								e.printStackTrace();
							}
						}
					});
				}catch(JMSException e){
					e.printStackTrace();
				}
			}
		});
	}


}
