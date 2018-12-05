package org.activiti.designer.test;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.FileInputStream;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.runtime.Execution;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.engine.test.ActivitiRule;
import org.junit.Rule;
import org.junit.Test;

public class ProcessTestMessageDrivenProcessDemo1 {

	private String filename = "/Users/sjl/Projects/demo/activiti-demo/src/main/resources/diagrams/Message-Driven-Process-Demo1.bpmn";

	@Rule
	public ActivitiRule activitiRule = new ActivitiRule();

	private static String MSG_PAID = "PAID";
	private static String MSG_WITHDRAWN = "WITHDRAWN";
	private static String CASHIER_CANDIDATE_GROUP = "CashierGroup";
	private static String ACOUNTANT_CANDIDATE_GROUP = "AccountantGroup";

	/*
	 * @Test public void startProcess() throws Exception { RepositoryService
	 * repositoryService = activitiRule.getRepositoryService();
	 * repositoryService.createDeployment().addInputStream(
	 * "Message-Driven-Process-Demo1.bpmn20.xml", new
	 * FileInputStream(filename)).deploy(); RuntimeService runtimeService =
	 * activitiRule.getRuntimeService(); Map<String, Object> variableMap = new
	 * HashMap<String, Object>(); variableMap.put("name", "Activiti");
	 * ProcessInstance processInstance =
	 * runtimeService.startProcessInstanceByKey("Message-Driven-Process-Demo1",
	 * variableMap); assertNotNull(processInstance.getId());
	 * System.out.println("id " + processInstance.getId() + " " +
	 * processInstance.getProcessDefinitionId()); }
	 */

	@Test
	public void testSendMessageEvent() {
		ProcessEngine engine = ProcessEngines.getDefaultProcessEngine();
		RepositoryService repositoryService = engine.getRepositoryService();
		RuntimeService runtimeService = engine.getRuntimeService();
		TaskService taskService = engine.getTaskService();

		System.out.println("----------Test SEND MESSAGE For Message Driven Process------------------------");
		List<ProcessInstance> processInstances = runtimeService.createProcessInstanceQuery()
				.processDefinitionKey("Message-Driven-Process-Demo1").list();
		if (processInstances != null) {
			for (ProcessInstance processInstance : processInstances) {
				String processInstanceId = processInstance.getId();
				System.out.println("processInstanceId " + processInstanceId + ", processDefinitionId "
						+ processInstance.getProcessDefinitionId() + ", processDefinitionKey "
						+ processInstance.getProcessDefinitionKey());

				List<Execution> executionList = runtimeService.createExecutionQuery()
						.processInstanceId(processInstanceId).messageEventSubscriptionName(MSG_PAID).list();

				if (executionList != null) {
					System.out.println("Found MSG_PAID");
					for (Execution execution : executionList) {
						String executionId = execution.getId();
						System.out.println(
								"---------------------id of execution " + executionId + ", activityId of execution "
										+ execution.getActivityId() + ", name of execution " + execution.getName());
						runtimeService.messageEventReceived(MSG_PAID, executionId, null);
					}
				}

				executionList = runtimeService.createExecutionQuery().processInstanceId(processInstanceId)
						.messageEventSubscriptionName(MSG_WITHDRAWN).list();

				if (executionList != null) {
					System.out.println("Found MSG_WITHDRAWN");
					for (Execution execution : executionList) {
						String executionId = execution.getId();
						System.out.println(
								"---------------------id of execution " + executionId + ", activityId of execution "
										+ execution.getActivityId() + ", name of execution " + execution.getName());
						runtimeService.messageEventReceived(MSG_WITHDRAWN, executionId, null);
					}
				}

			}
		}
	}

	@Test
	public void testUserTask() {
		ProcessEngine engine = ProcessEngines.getDefaultProcessEngine();
		RepositoryService repositoryService = engine.getRepositoryService();
		RuntimeService runtimeService = engine.getRuntimeService();
		TaskService taskService = engine.getTaskService();

		System.out.println("----------Test USER TASK FOR Message Driven Process------------------------");
		List<ProcessInstance> processInstances = runtimeService.createProcessInstanceQuery()
				.processDefinitionKey("Message-Driven-Process-Demo1").list();
		if (processInstances != null) {
			for (ProcessInstance processInstance : processInstances) {
				String processInstanceId = processInstance.getId();
				System.out.println("processInstanceId " + processInstanceId + ", processDefinitionId "
						+ processInstance.getProcessDefinitionId() + ", processDefinitionKey "
						+ processInstance.getProcessDefinitionKey());

				List<Task> taskList = taskService.createTaskQuery().processInstanceId(processInstanceId).taskCandidateGroup(CASHIER_CANDIDATE_GROUP).list();

				if (taskList != null) {
					System.out.println("Found tasks for " + CASHIER_CANDIDATE_GROUP);
					for (Task task : taskList) {
						String taskId = task.getId();
						String assignee = task.getAssignee();
						System.out.println(
								"---------------------id of task " + taskId + ", definition key of task "
										+ task.getTaskDefinitionKey() + ", name of execution " + task.getName() + ", assignee of task " + assignee);
						taskService.complete(taskId);
					}
				}

				taskList = taskService.createTaskQuery().processInstanceId(processInstanceId).taskCandidateGroup(ACOUNTANT_CANDIDATE_GROUP).list();

				if (taskList != null) {
					System.out.println("Found tasks for " + ACOUNTANT_CANDIDATE_GROUP);
					for (Task task : taskList) {
						String taskId = task.getId();
						String assignee = task.getAssignee();
						System.out.println(
								"---------------------id of task " + taskId + ", definition key of task "
										+ task.getTaskDefinitionKey() + ", name of execution " + task.getName() + ", assignee of task " + assignee);
						taskService.complete(taskId);
					}
				}

			}
		}

	}
}