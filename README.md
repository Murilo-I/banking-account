# Segurança

> A API realiza autenticação via JWT (JSON Web Token). A geração do token é feita com um algoritmo RSA (Rivest-Shamir-Adleman) 
> com uma assinatura SHA-512. Para tal, foi criada uma chave usando o keytool do java, armazenando-a num arquivo JKS.
> Comando para geração da chave: `keytool -genkeypair -alias bbdctoken -keyalg RSA -keypass your_key_pass -keystore bbdctoken.jks -storepass your_store_pass.`
> <br> Você pode armazenar o arquivo gerado no diretório que achar mais conveniente, porém, lembre-se de alterar o path no arquivo application-dev.yml;
> Já há um arquivo para ser utilizado durante os teste dentro do repositório test/resources.

# Banco de Dados

> O banco utilizado para desenvolvimento e produção é o mysql, enquanto que nos testes é utilizado o H2;
> <br> Não há necessidade de se preocupar com a criação das tabelas, o hibernate está configurado para gerar automaticamente os scripts ddl,
> contudo, é preciso criar o banco: `create database your_db;`.

# Variáveis de ambiente

> A aplicação contém 3 variáveis de ambiente que precisam ser definidas antes de executar a aplicação local, assim como no arquivo docker-compose.yml
> 1. MYSQL_CONNECTION_STRING:
>     - URI para conexão com o banco de dados, no ambiente de desenvolvimento, deve conter usuário e senha, em produção a senha está separada no docker-compose;
>     - Embora utilizar um secure vault fosse ideal.
> 3. JKS_KEY_PASS:
>     - Senha do keypass definida na criação do arquivo .jks
> 4. JKS_STORE_PASS
>     - Senha do storepass definida na criação do arquivo .jks

# Build

> A api utiliza maven, portanto, para buildá-la, utilize o comando `mvn clean package`, isto irá criar a pasta target com o .jar da aplicação,
> dentro deste diretório, copie o arquivo .jks para ser utilizado durante o docker build.

# Execução / Docker Compose

> Rode o comando `docker compose up --build` para criar as imagens e rodar a aplicação;
> <br> A api local roda na porta 9090, porém, no docker ela está sendo disponibilizada na porta 9070.

## Testes Local / Postman Collection

> Sujeito a adaptações;
> <br>Lembre-se de copiar o valor do cookie CSRF-TOKEN para o Header X-XSRF-TOKEN sempre que este for gerado.

```json
{
	"info": {
		"_postman_id": "790fda94-92e4-418f-be4b-e8d9bb42c29c",
		"name": "banking",
		"schema": "https://schema.getpostman.com/json/collection/v2.1.0/collection.json",
		"_exporter_id": "7772801"
	},
	"item": [
		{
			"name": "create-account",
			"request": {
				"method": "POST",
				"header": [],
				"body": {
					"mode": "raw",
					"raw": "{\r\n    \"holderName\": \"Mr Generic\",\r\n    \"email\": \"mr.generic@email.com\",\r\n    \"password\": \"generic_pass.2569\"\r\n}",
					"options": {
						"raw": {
							"language": "json"
						}
					}
				},
				"url": {
					"raw": "http://localhost:9090/banking/account",
					"protocol": "http",
					"host": [
						"localhost"
					],
					"port": "9090",
					"path": [
						"banking",
						"account"
					]
				}
			},
			"response": []
		},
		{
			"name": "gen-token",
			"request": {
				"method": "POST",
				"header": [
					{
						"key": "X-XSRF-TOKEN",
						"value": "85a3c4e4-97b5-45ac-a345-0cc45e0c8e13",
						"type": "text"
					}
				],
				"body": {
					"mode": "raw",
					"raw": "{\r\n    \"email\": \"generic@email.com\",\r\n    \"password\": \"123456\"\r\n}",
					"options": {
						"raw": {
							"language": "json"
						}
					}
				},
				"url": {
					"raw": "http://localhost:9090/banking/auth",
					"protocol": "http",
					"host": [
						"localhost"
					],
					"port": "9090",
					"path": [
						"banking",
						"auth"
					]
				}
			},
			"response": []
		},
		{
			"name": "deposit",
			"request": {
				"auth": {
					"type": "bearer",
					"bearer": [
						{
							"key": "token",
							"value": "eyJhbGciOiJSUzUxMiJ9.eyJpc3MiOiJCUkFERVNDTy1JTlZFU1RJTkciLCJzdWIiOiIzIiwiaWF0IjoxNzM0ODQwODc5LCJleHAiOjE3MzQ4NDk4Nzl9.qHFartNAPzPbiE3HaDpYIStLl8ix7PwQ2RPbKtuf9JS_eiI7YSDYFTL4VG4ALRjNjmpNrsz8B18gmQ_Xe4_YIwi9842NLYFe_pwsTRRSjh47v4gyTbJHqPQbcmifJRI7UiNhFmMA8Z5_in8Xtx4_Jt7qYbS1Kl6DNBq45gkZLnKsTFHA7eN_9WLuDTawVdz4suWt6cCTmXwX8h4F-VcMx8lPJsSt_5B5fcoJYmOZOM7FuJHzxSqRkUrxjSXppybY2irNUR1T8qHT9zE2hcpfqhz_g3QmT8ICUNVzcHOAvyZcz8IjsiLwsCpNLEjppnbMnsBf81fDhjv2km3VvhUwgg",
							"type": "string"
						}
					]
				},
				"method": "POST",
				"header": [],
				"body": {
					"mode": "raw",
					"raw": "{\r\n    \"amount\": 10000\r\n}",
					"options": {
						"raw": {
							"language": "json"
						}
					}
				},
				"url": {
					"raw": "http://localhost:9090/banking/account/deposit?originAccountNumber=3",
					"protocol": "http",
					"host": [
						"localhost"
					],
					"port": "9090",
					"path": [
						"banking",
						"account",
						"deposit"
					],
					"query": [
						{
							"key": "originAccountNumber",
							"value": "3"
						}
					]
				}
			},
			"response": []
		},
		{
			"name": "transfer",
			"request": {
				"auth": {
					"type": "bearer",
					"bearer": [
						{
							"key": "token",
							"value": "eyJhbGciOiJSUzUxMiJ9.eyJpc3MiOiJCUkFERVNDTy1JTlZFU1RJTkciLCJzdWIiOiIzIiwiaWF0IjoxNzM0NzM2NzUyLCJleHAiOjE3MzQ3NDU3NTJ9.td4C08x1pMkRpkimuHRH2Ykh2CuyV9_E1q3dO31ZFoQfLP5PkidacDqvvWIHr3NvUm5Tq9LZJE9CYJtW1xrUnWVTKzIVP_IcHei9m5I5ot3irMS2nfmKWl785HjtyovaP8NUm4bF0UN8ece2_cX3kYxpOYiyXYoBYftUK1-Uk0w7fGRKA32rhad2OHJRgdYVnkKIhtX1uIOdQnkNDM3uOqJnD1Pu04m3pa2dDPTZdZeUd30lG1iahwJgzSVf66JixxSLHmw0ZL8v4vWbFVGfIaq0CqhckbJ3Om0ooJx4v-BfYgSywfwwsjJ3cN6_eKlE4S5Uz6_XNhIayMHFZZiuFw",
							"type": "string"
						}
					]
				},
				"method": "POST",
				"header": [],
				"body": {
					"mode": "raw",
					"raw": "{\r\n    \"accountTo\": 2,\r\n    \"amount\": 1000\r\n}",
					"options": {
						"raw": {
							"language": "json"
						}
					}
				},
				"url": {
					"raw": "http://localhost:9090/banking/account/transfer?originAccountNumber=3",
					"protocol": "http",
					"host": [
						"localhost"
					],
					"port": "9090",
					"path": [
						"banking",
						"account",
						"transfer"
					],
					"query": [
						{
							"key": "originAccountNumber",
							"value": "3"
						}
					]
				}
			},
			"response": []
		},
		{
			"name": "statement",
			"request": {
				"auth": {
					"type": "bearer",
					"bearer": [
						{
							"key": "token",
							"value": "eyJhbGciOiJSUzUxMiJ9.eyJpc3MiOiJCUkFERVNDTy1JTlZFU1RJTkciLCJzdWIiOiIzIiwiaWF0IjoxNzM0NzM2NzUyLCJleHAiOjE3MzQ3NDU3NTJ9.td4C08x1pMkRpkimuHRH2Ykh2CuyV9_E1q3dO31ZFoQfLP5PkidacDqvvWIHr3NvUm5Tq9LZJE9CYJtW1xrUnWVTKzIVP_IcHei9m5I5ot3irMS2nfmKWl785HjtyovaP8NUm4bF0UN8ece2_cX3kYxpOYiyXYoBYftUK1-Uk0w7fGRKA32rhad2OHJRgdYVnkKIhtX1uIOdQnkNDM3uOqJnD1Pu04m3pa2dDPTZdZeUd30lG1iahwJgzSVf66JixxSLHmw0ZL8v4vWbFVGfIaq0CqhckbJ3Om0ooJx4v-BfYgSywfwwsjJ3cN6_eKlE4S5Uz6_XNhIayMHFZZiuFw",
							"type": "string"
						}
					]
				},
				"method": "GET",
				"header": [],
				"url": {
					"raw": "http://localhost:9090/banking/account/3",
					"protocol": "http",
					"host": [
						"localhost"
					],
					"port": "9090",
					"path": [
						"banking",
						"account",
						"3"
					]
				}
			},
			"response": []
		}
	]
}
```
