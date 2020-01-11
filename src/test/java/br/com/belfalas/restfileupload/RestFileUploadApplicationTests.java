package br.com.belfalas.restfileupload;

import br.com.belfalas.restfileupload.controller.FileUploadController;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class RestFileUploadApplicationTests {

	@Autowired
	private FileUploadController fileUploadController;

	@Test
	void contextLoads() {
		assertThat(fileUploadController).isNotNull();
	}

}
