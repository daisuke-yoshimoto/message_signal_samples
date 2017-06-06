package message_signal_samples;

import static org.junit.Assert.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.activiti.engine.HistoryService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngineConfiguration;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.history.HistoricVariableInstance;
import org.activiti.engine.runtime.Execution;
import org.activiti.engine.runtime.ProcessInstance;

import org.junit.Test;

public class SignalThrowingInExecutionSample {

	@Test
	public void throwToSignalInEventGateway() throws FileNotFoundException {
		// Build process engine
		ProcessEngine processEngine = ProcessEngineConfiguration.createStandaloneInMemProcessEngineConfiguration().buildProcessEngine();
		RepositoryService repositoryService = processEngine.getRepositoryService();
		RuntimeService runtimeService = processEngine.getRuntimeService();
		HistoryService historyService = processEngine.getHistoryService();
		
		// Deploy
		String filename = System.getProperty("user.dir") + "/src/main/resources/diagrams/event_gateway_signal.bpmn";
		repositoryService.createDeployment().addInputStream("myProcess.bpmn20.xml", new FileInputStream(filename)).deploy();
		
		// Create process and the execution of signal event was generated.
		ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("catchSignal");
		
		// Throw signal event
		Execution execution = runtimeService.createExecutionQuery().processInstanceId(processInstance.getId()).signalEventSubscriptionName("alert").singleResult();
		runtimeService.signalEventReceived("alert", execution.getId());
		
		// Check result
		HistoricVariableInstance result = historyService.createHistoricVariableInstanceQuery().processInstanceId(processInstance.getId()).variableName("result").singleResult();
		assertEquals("Handle alert", result.getValue());
	}

}
