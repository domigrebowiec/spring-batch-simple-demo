# Simple Spring Batch project

1. To run export-people job send POST request to http://localhost:8080/v1/api/run/export-people with json

```
{
	"fileName": "output_with_exception",
	"timestamp": "1234556781"
}
```


2. To run import-people job send POST request to http://localhost:8080/v1/api/run/import-people