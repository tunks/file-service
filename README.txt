File service :

A simple spring-boot service for uploading and downloading files of any type


1. Build

  i. Clone and use maven to build the source code
     mvn clean install

2. Run
   i. mvn spring-boot:run

3. Testing the endpoints

   i. To upload a file
      curl -F 'file=@/Users/ebrimatunkara/Downloads/learning_core_audio.pdf' http://localhost:18080/api/files?createdby=user1
      
   ii. To download file by name
      curl http://localhost:18080/api/files/download/learning_core_audio.pdf
      
      or
      
      wget http://localhost:18080/api/files/download/learning_core_audio.pdf
   
   iii. To get list of file versions by filename
       curl http://localhost:18080/api/files?name=learning_core_audio.pdf
 