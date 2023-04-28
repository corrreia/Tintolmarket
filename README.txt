-------------------------- Grupo 03 -----------------------------
--------------------- SegC-grupo03-proj1 ------------------------

 - Elementos do grupo:
	- Miguel Pato - fc57102
    - Tomás Correia - fc56372
    - João Vieira - fc45677

------------------------- Instruções ----------------------------

1 - Unzip do ficheiro SegC-grupo03-proj1-fase2.zip

2 - Para compilar e executar o programa temos duas opções:

    2.1 - Compilar com vscode
        - Melhor forma: Aceder à command pallette (CTRL+SHIFT+P) e selecionar
        "Java: Export Jar" e selecionar uma das classes main (TintolmarketServer ou Tintolmarket)
        NOTA: Pelo settings.json o ficheiro jar criado irá ficar com o nome do ficheiro aberto no IDE.

    2.2 - Compilar com o eclipse
        - Carregar com o botão direito e exportar como jar.

3 - Para executar o programa temos 3 opções:

    3.1 Terminal
    - Para executar o servidor TintolmarketServer:
        - ir à diretoria jars e no terminal escrever:
            java -jar TintolmarketServer.jar <port> <cipherPassword> <keyStoreName> <keyStorePassword>"

    - Para executar o cliente Tintolmarket:
        - ir à diretoria jars e no terminal escrever:
            java -jar Tintolmarket.jar <server_address>:<port> <truststore> <keystore> <keystore_password> <user_id>

    3.2 VScode
    - Na pasta ".vscode" está um ficheiro "launch.json" que permite correr através do próprio IDE.
        - Ir à aba "Run and Debug" e correr. NOTA: Há dois perfis pré-feitos para o cliente e um para o servidor.

    3.3 Eclipse
    - Para executar o servidor TintolmarketServer:
        - Run configurations com arguments: 12345 <cipherPassword> <keyStoreName> <keyStorePassword>"

    - Para executar o cliente Tintolmarket:
        - Run configurations com arguments: 127.0.0.1:12345 <truststore> <keystore> <keystore_password> <user_id>

------------------------- Limitações -----------------------------

  - Não é feita a encriptação de mensagens.
  - Também não há a verificação da integridade dos ficheiros.
  - Por vezes, na permeira execução há um problema de autorização no cliente, quando acontece basta reiniciar o cliente.

--------------------- Estrutura do Projeto -----------------------

 - Tintolmarket
    -src 
        - exceptions
        - handlers
        - main
        - security

 -TintolmarketServer
    -src
        - exceptions
        - handlers
        - main
        - objects
        - security

---------------------------- Notas --------------------------------

 - Quando corremos o servidor e o cliente, dentro da diretoria dos jars são criados
 todos os ficheiros necessários para a execução do programa, incluindo os ficheiros .ser
 que contêm os dados dos utilizadores e dos vinhos e ainda o ficheiro "users.txt"
 que tem os pares <username>:<password> dos utilizadores registados.

 - No comando add <wine> <image> o <image> é o caminho para a imagem do vinho e deve ser possível
 enviar uma imagem de qualquer localização do computador. Aquando do envio da imagem, na pasta
 "jars" é criada uma pasta "serverWineImages" que contém todas as imagens enviadas
 pelo cliente e recebidas pelo servidor.

 - Temos pastas com os ficheiros de cada user na pasta jars/client/Clients. Cada pasta tem a keystore
 e o certificado. 

 - O ficheiro "users.txt" contém os pares <username>:<cert> que está encriptado.

 - Temos também a nova funcionalidade da blockchain onde é gerada a pasta blockchain com os ficheiros .blk 
que contêm as transações.

 