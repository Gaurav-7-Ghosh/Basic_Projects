from fastapi import FastAPI
from langchain_core.prompts import ChatPromptTemplate
from langchain_core.output_parsers import StrOutputParser
from langchain_groq import ChatGroq
from langchain_core.messages import HumanMessage,SystemMessage

from langserve import add_routes 
import os 
##Loading the environment variables from the .env file
from dotenv import load_dotenv
load_dotenv()

##Output Parser to convert the output to string
from langchain_core.output_parsers import StrOutputParser
parser = StrOutputParser()
## Storing the groq api key in a variable (check expiry)
groq_api_key = os.getenv("GROQ_API_KEY")

model = ChatGroq(model="llama-3.1-8b-instant",groq_api_key=groq_api_key)

generic_template = "Translate the following text from english to:  {language}"
prompt=ChatPromptTemplate.from_messages([("system",generic_template),("human","{text}")])

## Creating the Chain with the model, prompt and output parser
chain = prompt | model | parser

##App Defintion
app = FastAPI(title="Simple LLM API",version="0.01",
              description="A simple API to demonstrate the use of LLMs with FastAPI and LangServe using LangChain runnable interfaces")

## Adding the FastAPI routes to the chain
add_routes(app,chain,path="/chain")

if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app,host="localhost",port=8005)