# Finagle ZooKeeperServerSetCluster demo

Finagle Hack-a-thon at Twitter Japan @yakitori.

## Demo

Check the YouTube video.

http://youtu.be/mlbJQziTJTk

screenshot:

![screen shot](https://github.com/seratch/finagle-cluster-demo/raw/master/screen_shot.png)


# Requirement

- Apache ZooKeeper 3.4.3

ZooKeeper should be already runnning at localhost:2181.

```
brew install zookeeper
cp -p /usr/local/etc/zookeeper/zoo_sample.cfg /usr/local/etc/zookeeper/zoo.cfg
sudo zkServer start
```

- Play! framework 2.0.1

```
brew install play
```


# How to run?

- play run

```
git clone git://github.com/seratch/finagle-cluster-demo.git
cd finagle-cluster-demo
play start
```

And then, access http://localhost:9000/ from your browser.

