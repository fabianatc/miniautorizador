# *Mini Autorizador*

O **Mini Autorizador** é um sistema desenvolvido em Java com o objetivo de gerenciar transações e autorizações de
cartões de forma simples e eficaz.
Este projeto utiliza as melhores práticas de desenvolvimento e é baseado em uma arquitetura modular e escalável.

## *Requisitos*

- Essa solução precisa ser desenvolvida usando Java, Maven e preferência ao Spring Boot como framework principal.
- Criar o projeto no Github.
- Documentar tudo o que julgar necessário e interessante.
- Implementar os testes automatizados com alta cobertura.

#### 1. Contexto

Processamento todos os dias diversas transações no cartão.
De forma breve, as transações saem das maquininhas de cartão e chegam até uma de nossas aplicações, conhecida como
*autorizador*, que realiza uma série de verificações e análises. Essas também são conhecidas como *regras de
autorização*.

Ao final do processo, o autorizador toma uma decisão, aprovando ou não a transação:

* se aprovada, o valor da transação é debitado do saldo disponível e informamos à maquininha que tudo ocorreu bem.
* senão, apenas informamos o que impede a transação de ser feita e o processo se encerra.

Tarefa: construir um *mini-autorizador*. Este será uma aplicação Spring Boot com interface totalmente REST que permita:

* a criação de cartões (todo cartão deverá ser criado com um saldo inicial de R$500,00)
* a obtenção de saldo do cartão
* a autorização de transações realizadas usando os cartões previamente criados como meio de pagamento

#### 2. Regras de autorização a serem implementadas

Uma transação pode ser autorizada se:

* o cartão existir
* a senha do cartão for a correta
* o cartão possuir saldo disponível

Caso uma dessas regras não seja atendida, a transação não será autorizada.

#### 3. Demais instruções

- O projeto contém um docker-compose.yml com 1 banco de dados relacional e outro não relacional e deve-se utilizar um
  deles.
- Não é necessário persistir a transação. Mas é necessário persistir o cartão criado e alterar o saldo do cartão caso
  uma transação seja autorizada pelo sistema.

Serão realizados os seguintes testes, nesta ordem:

* criação de um cartão
* verificação do saldo do cartão recém-criado
* realização de diversas transações, verificando-se o saldo em seguida, até que o sistema retorne informação de saldo
  insuficiente
* realização de uma transação com senha inválida
* realização de uma transação com cartão inexistente

Esses testes serão realizados:

* rodando o docker-compose
* rodando a aplicação

#### 4. Contratos dos serviços

- **Criar novo cartão**

```
Method: POST
URL: http://localhost:8080/cartoes
Body (json):
{
    "numeroCartao": "6549873025634501",
    "senha": "1234"
}
Autenticação: BASIC, com login = username e senha = password
```

*Possíveis respostas*

```
Criação com sucesso:
   Status Code: 201
   Body (json):
   {
      "senha": "1234",
      "numeroCartao": "6549873025634501"
   } 
-----------------------------------------
Caso o cartão já exista:
   Status Code: 422
   Body (json):
   {
      "senha": "1234",
      "numeroCartao": "6549873025634501"
   }
-----------------------------------------
Erro de autenticação: 401 
```

- **Obter saldo do Cartão**

```
Method: GET
URL: http://localhost:8080/cartoes/{numeroCartao} , onde {numeroCartao} é o número do cartão que se deseja consultar
Autenticação: BASIC, com login = username e senha = password
```

*Possíveis respostas*

```
Obtenção com sucesso:
   Status Code: 200
   Body: 495.15 
-----------------------------------------
Caso o cartão não exista:
   Status Code: 404 
   Sem Body
-----------------------------------------
Erro de autenticação: 401 
```

- **Realizar uma Transação**

```
Method: POST
URL: http://localhost:8080/transacoes
Body (json):
{
    "numeroCartao": "6549873025634501",
    "senhaCartao": "1234",
    "valor": 10.00
}
Autenticação: BASIC, com login = username e senha = password
```

*Possíveis respostas*

```
Transação realizada com sucesso:
   Status Code: 201
   Body: OK 
-----------------------------------------
Caso alguma regra de autorização tenha barrado a mesma:
   Status Code: 422 
   Body: SALDO_INSUFICIENTE|SENHA_INVALIDA|CARTAO_INEXISTENTE (dependendo da regra que impediu a autorização)
-----------------------------------------
Erro de autenticação: 401 
```

Desafios (não obrigatórios):

* é possível construir a solução inteira sem utilizar nenhum if. Só não pode usar *break* e *continue*! Conceitos de
  orientação a objetos ajudam bastante!
* como garantir que 2 transações disparadas ao mesmo tempo não causem problemas relacionados à concorrência?
  Exemplo: dado que um cartão possua R$10.00 de saldo. Se fizermos 2 transações de R\$ 10.00 ao mesmo tempo, em
  instâncias diferentes da aplicação, como o sistema deverá se comportar?

## *Implementação da aplicação*

#### 1. Estrutura e Organização do Código

- Projeto está organizado de forma modular, com camadas bem definidas (Controller, Service, Repository, Model,
  Interfaces etc.), seguindo a separação de responsabilidades e boas práticas de estruturação.

#### 2. Uso de Boas Práticas de Desenvolvimento

- Dependency Injection foi utilizada, para tornar o código modular e facilitar a testabilidade.
- Uso de DTOs para transferência de dados entre camadas.
- Tratamento de Exceções: Camada de tratamento centralizado de exceções (@RestControllerAdvice) para lidar com erros e
  evitar a propagação de exceções genéricas para o cliente.

#### 3. Escolha e Configuração das Tecnologias

- **Java 21**
- **Spring Boot 3.4.1**: Framework para desenvolvimento de aplicações Java.
- **Maven**: Gerenciador de dependências e build.
- **Mysql Database**: Banco de dados mysql principal.
- **H2 Database**: Banco de dados em memória para testes.
- **Spring Data JPA**: Abstração para interações com o banco de dados.
- **Springdoc OpenAPI**: Para geração de documentação automática da API (Swagger UI).
- **Spring Security**: Para autenticação e autorização.
- **Liquibase**: Gerenciador de scripts de banco de dados.

* Foi escolhido o banco mysql disponível no docker-compose por questões de maior familiaridade com banco de dados
  relacionais.
* H2 Database foi escolhido para testes, porém num ambiente de produção podemos usar banco Mysql ou qualquer outro que
  esteja sendo utilizado no projeto.

#### 4. Escalabilidade e Extensibilidade

- Apesar da aplicação ter poucas funcionalidades no início, foi estruturada na arquitetura DDD, pois separa a lógica de
  negócios em serviços e por ser escalável para futuras implementações.

#### 5. Testabilidade

- O projeto contém testes unitários para as principais classes e métodos e de integração para as rest controllers.
- Foi aplicada alta cobertura de testes para garantir a qualidade do código.

#### 6. Documentação

- O projeto inclui documentação com Springdoc OpenAPI, o que facilita o entendimento e uso da API. Disponível
  em: http://localhost:8080/swagger-ui/index.html
- Nas classes principais foram adicionados comentários.

#### 7. Desafios

- Uso de IFs: consta apenas um IF de verificação de match de senha.
- Como garantir que 2 transações disparadas ao mesmo tempo não causem problemas relacionados à concorrência?
  Foi utilizada a técnica de tratamento de concorrência otimista no método de atualizar saldo, aplicando o Optimistic
  Locking para lidar com cenários em que múltiplas transações ou threads tentam acessar e modificar o mesmo recurso
  simultaneamente.
  No Optimistic Locking, a entidade Cartao possui um campo de versão (@Version) que é incrementado automaticamente pelo
  JPA/Hibernate sempre que a entidade é atualizada.
  Antes de salvar uma entidade, o JPA/Hibernate verifica se a versão da entidade no banco de dados é a mesma que a
  versão carregada na memória. Isso garante que nenhuma outra transação tenha modificado a entidade entre o momento em
  que ela foi carregada e o momento em que a tentativa de salvamento ocorre.
  Se a versão no banco de dados não corresponder à versão carregada na memória, o Hibernate lança uma exceção
  OptimisticLockingFailureException (ou OptimisticLockException), indicando um conflito de concorrência.
  Consegui simular via runner do postman a concorrência e tive o comportamento esperado de indicação de conflito e não
  atualização do saldo.
- Outra técnica de tratamento de concorrência: uso de mensageria (ou filas de mensagens) seria uma abordagem eficaz para
  lidar com cenários de grande volume de transações, especialmente quando múltiplas transações precisam ser processadas
  simultaneamente de forma consistente e escalável.

#### 8. Complementação

- Foi utilizada a encriptação da senha pra registro no banco de dados, para melhor segurança.
- Spring security foi utilizado para controle de acesso e autenticação.
- Além dos controles de erros solicitados e suas mensagens personalizadas, foram realizadas validações das entradas
  de dados (body) nulas e que não estavam de acordo com as regras de negócio (senhas com 4 digitos, cartão com 16
  digitos etc).
  Mensagens adotadas foram:
    - Request totalmente nula: REQUEST_INVALIDA (status 400 - Bad Request)
    - Campos nulos ou inválidos: DADOS_INVALIDOS (status 400 - Bad Request)

Desenvolvido por: Fabiana Costa