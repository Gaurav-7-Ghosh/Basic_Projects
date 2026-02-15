FROM node:21

WORKDIR  /AppDirectory
COPY package.json .
RUN npm install 

COPY server.js .
COPY index.html .
COPY images ./images


EXPOSE 3000

CMD [ "node", "server.js" ]