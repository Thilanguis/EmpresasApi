package br.com.cotiinformatica.controllers;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import br.com.cotiinformatica.entities.Empresa;
import br.com.cotiinformatica.repositories.IEmpresaRepository;
import br.com.cotiinformatica.requests.EmpresasPostRequest;
import br.com.cotiinformatica.requests.EmpresasPutRequest;
import io.swagger.annotations.ApiOperation;

@Transactional
@Controller
public class EmpresasController {

	/*
	 * HTTP 200, dentro dessa faixa indica sucesso. (SUCCESS) 200 OK 201 CREATED 204
	 * NO CONTENT
	 * 
	 * HTTP 400, dentro dessa faixa indica erro gerado pelo cliente da api. (CLIENT ERROR) 400 BAD REQUEST 401 UNAUTHORIZED 403 FORBIDDEN 404 NOT FOUND
	 * 
	 * HTTP 500, dentro dessa faixa indica erro no servidor. (SERVER ERROR)
	 */

	private static final String ENDPOINT = "/api/empresas";

	@Autowired // A interface será inicializada automaticamente
	private IEmpresaRepository empresaRepository;

	@ApiOperation("Método para realizar cadastro de uma empresa")
	@RequestMapping(value = ENDPOINT, method = RequestMethod.POST)

	public ResponseEntity<String> post(@RequestBody EmpresasPostRequest request) {

		try {
			
			//verificar se o cnpj informado já pertence a alguma empresa cadastrada
			if (empresaRepository.findByCnpj(request.getCnpj()) != null) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("O Cnpj " + request.getCnpj() + " já existe");
			}
			
			//verificar se o Razão Social informado já pertence a alguma empresa cadastrada
			if (empresaRepository.findByRazaoSocial(request.getRazaoSocial()) != null) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("A Razão Social " + request.getRazaoSocial() + " já existe");
			}

			Empresa empresa = new Empresa();

			empresa.setNomeFantasia(request.getNomeFantasia());
			empresa.setRazaoSocial(request.getRazaoSocial());
			empresa.setCnpj(request.getCnpj());

			empresaRepository.save(empresa);

			return ResponseEntity.status(HttpStatus.CREATED).body("Empresa cadastrada com sucesso.");

		} catch (Exception e) {

			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());

		}

	}

	@ApiOperation("Método para realizar a edição de uma empresa")
	@RequestMapping(value = ENDPOINT, method = RequestMethod.PUT)

	public ResponseEntity<String> put(@RequestBody EmpresasPutRequest request) {

		try {

//			Empresa empresa = new Empresa();
//
//			empresa.setIdEmpresa(request.getIdEmpresa());
//			empresa.setNomeFantasia(request.getNomeFantasia());
//			empresa.setRazaoSocial(request.getRazaoSocial());
//			empresa.setCnpj(request.getCnpj());
//
//			empresaRepository.save(empresa);
			
			//pesquisar a empresa pelo id
			Optional<Empresa> consulta = empresaRepository.findById(request.getIdEmpresa());
			
			//verificar se a empresa foi encontrada
			if(consulta.isEmpty()) {
				
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Nenhuma Empresa foi encontrada, verifique o id informado");
				
			}
			
			//capturar a empresa
			Empresa empresa = consulta.get();
			
			//modificando os dados da empresa
			empresa.setNomeFantasia(request.getNomeFantasia());
			empresaRepository.save(empresa);

			return ResponseEntity.status(HttpStatus.OK).body("Empresa atualizada com sucesso.");

		} catch (Exception e) {

			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());

		}

	}

	@ApiOperation("Método para realizar a exclusão de uma empresa")
	@RequestMapping(value = ENDPOINT + "/{idEmpresa}", method = RequestMethod.DELETE)

	public ResponseEntity<String> delete(@PathVariable("idEmpresa") Integer idEmpresa) {
		
		try {

			//pesquisar a empresa pelo id
			Optional<Empresa> consulta = empresaRepository.findById(idEmpresa);
			
			//verificar se a empresa foi encontrada
			if(consulta.isEmpty()) {
				
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Nenhuma Empresa foi encontrada, verifique o id informado");
				
			}
			
			//capturar a empresa
			Empresa empresa = consulta.get();
			
			//excluir a empresa
			empresaRepository.delete(empresa);

			return ResponseEntity.status(HttpStatus.OK).body("Empresa excluida com sucesso.");

		} catch (Exception e) {

			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());

		}


	}

	@ApiOperation("Método para realizar a consulta de todas as empresas")
	@RequestMapping(value = ENDPOINT, method = RequestMethod.GET)

	public ResponseEntity<List<Empresa>> getAll() {

		
		try {
			
			List<Empresa> empresas = (List<Empresa>) empresaRepository.findAll();
			
			return ResponseEntity.status(HttpStatus.OK).body(empresas);
			
		} catch (Exception e) {

			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null); 

		}

	}

	@ApiOperation("Método para realizar a consulta de uma empresa")
	@RequestMapping(value = ENDPOINT + "/{idEmpresa}", method = RequestMethod.GET)

	public ResponseEntity<Empresa> getById(@PathVariable("idEmpresa") Integer idEmpresa) {
		
		try {
			
			Optional<Empresa> consulta = empresaRepository.findById(idEmpresa);
			
			if(consulta.isPresent()) {
				
				Empresa empresa = consulta.get();
				return ResponseEntity.status(HttpStatus.OK).body(empresa);
				
			}
			
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
			
		} catch (Exception e) {

			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null); 

		}


	}
}