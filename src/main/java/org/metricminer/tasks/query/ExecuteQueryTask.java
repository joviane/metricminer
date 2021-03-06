package org.metricminer.tasks.query;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;
import org.apache.log4j.Logger;
import org.metricminer.config.MetricMinerConfigs;
import org.metricminer.infra.dao.QueryDao;
import org.metricminer.model.Query;
import org.metricminer.model.QueryResult;
import org.metricminer.model.Task;
import org.metricminer.model.TaskConfigurationEntryKey;
import org.metricminer.tasks.RunnableTask;

import br.com.caelum.vraptor.simplemail.Mailer;

public class ExecuteQueryTask implements RunnableTask {

    private final Long queryId;
    private final QueryExecutor queryExecutor;
    private final QueryDao queryDao;
    private final MetricMinerConfigs config;
    private Mailer mailer;
    private static Logger logger = Logger.getLogger(ExecuteQueryTask.class);

    public ExecuteQueryTask(Task task, QueryExecutor queryExecutor,
            QueryDao queryDao, MetricMinerConfigs config) {
        this.queryDao = queryDao;
        this.config = config;
        this.queryId = Long
                .parseLong(task
                        .getTaskConfigurationValueFor(TaskConfigurationEntryKey.QUERY_ID));
        this.queryExecutor = queryExecutor;
        this.mailer = config.getMailer();
    }

    @Override
    public void run() {
    	QueryResult result = null;
    	Query query = queryDao.findBy(queryId);
    	try {
	        String filename = config.getQueriesResultsDir() + "/result-"
	                + query.getId() + "-" + query.getResultCount();
	        ZipOutputStream zipOutputStream  = zipOutStreamFor(filename);
	        OutputStream csvOutputStream = csvOutStreamFor(filename);
	        queryExecutor.execute(query, zipOutputStream, csvOutputStream);
	        zipOutputStream.closeEntry();
	        zipOutputStream.close();
	        csvOutputStream.close();
	        
	        result = new QueryResult(filename, query);
            query.addResult(result);
            result.success();
        } catch (Exception e) {
        	result = new QueryResult(query);
            result.fail(ExceptionUtils.getStackTrace(e));
            query.addResult(result);
        }
        sendMail(query);
        queryDao.save(result);
        queryDao.update(query);
    }

    private OutputStream csvOutStreamFor(String filename) throws FileNotFoundException {
		return new FileOutputStream(new File(filename + ".csv"));
	}

	private ZipOutputStream zipOutStreamFor(String filename) throws IOException {
    	FileOutputStream fileOutputStream = createFile(filename + ".zip");
        ZipOutputStream zipOutputStream = new ZipOutputStream(fileOutputStream);
        zipOutputStream.putNextEntry(new ZipEntry("result.csv"));
		return zipOutputStream;
	}

	private void sendMail(Query query) {
        String email = query.getAuthor().getEmail();
        try {
            SimpleEmail simpleEmail = new SimpleEmail();
            simpleEmail.addTo(email);
            simpleEmail.setSubject("Your query '" + query.getName() + "' at metricminer.org.br has finished!");
            simpleEmail.setMsg("Go to metricminer.org.br/query/" + query.getId() + " and download the results");
            mailer.send(simpleEmail);
        } catch (EmailException e) {
            logger.error("Could not send email to: " + email);
        }
    }

    private FileOutputStream createFile(String tmpFileName) {
        try {
            return new FileOutputStream(tmpFileName);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

}
