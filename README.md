# WebHDFS
A Library that implements the WebHDFS REST API: https://hadoop.apache.org/docs/r1.0.4/webhdfs.html.

Additionally webhdfs has a workflow builder with a fluent interface that manages the life-cycle of a file or directory. This means that a user can: create a directory; change its owner and permissions; create and write to file and change its ownership and permissions in a single workflow. Note: all the methods in WebHDFS REST API are not implemented. However, they may be added whenever needed.