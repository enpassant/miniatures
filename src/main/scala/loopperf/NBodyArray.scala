package loopperf

object NBodyArray extends App {
	def timeCode[T](warmups: Int, timeRuns: Int)(body: => T): Seq[Double] = {
		for(_ <- 1 to warmups) body
		for(_ <- 1 to timeRuns) yield {
			val start = System.nanoTime()
			body
			(System.nanoTime()-start)*1e-9
		}
	}
	def printTimeInfo(times: Seq[Double]): Unit = {
		val mean = times.sum/times.length
		val rms = math.sqrt(times.map(x => (x-mean)*(x-mean)).sum/times.length)
		println(s"mean = $mean, rms = $rms")
	}

	val numBodies = 1000
	val positions = Array.fill(numBodies*3)(0.0)
	val velocities = Array.fill(numBodies*3)(0.0)
	val accel = Array.fill(numBodies*3)(0.0)
	val masses = Array.fill(numBodies)(1e-10)
	masses(0) = 1.0
	class Particle(val index: Int) extends AnyVal {
		def x = positions(index*3)
		def y = positions(index*3+1)
		def z = positions(index*3+2)
		def vx = velocities(index*3)
		def vy = velocities(index*3+1)
		def vz = velocities(index*3+2)
		def ax = accel(index*3)
		def ay = accel(index*3+1)
		def az = accel(index*3+2)
		def mass = masses(index)
		def x_=(d: Double): Unit = positions(index*3) = d
		def y_=(d: Double): Unit = positions(index*3+1) = d
		def z_=(d: Double): Unit = positions(index*3+2) = d
		def vx_=(d: Double): Unit = velocities(index*3) = d
		def vy_=(d: Double): Unit = velocities(index*3+1) = d
		def vz_=(d: Double): Unit = velocities(index*3+2) = d
		def ax_=(d: Double): Unit = accel(index*3) = d
		def ay_=(d: Double): Unit = accel(index*3+1) = d
		def az_=(d: Double): Unit = accel(index*3+2) = d
	}

	val dt = 0.01
	initArrays()
	println("for:")
	printTimeInfo(timeCode(5,20){ forSim(100) })
	initArrays()
	println("while:")
	printTimeInfo(timeCode(5,20){ whileSim(100) })

	def initArrays(): Unit = {
		for(i <-  1 until numBodies) {
			val p = new Particle(i)
			p.x = i
			p.vy = math.sqrt(1.0/i)
		}
	}

	def forSim(steps: Int): Unit = {
		for(_ <- 1 to steps) {
			for(i <- 0 until numBodies*3) {
				accel(i) = 0.0
			}
			for {
				i <- 0 until numBodies
				j <- i+1 until numBodies
			} {
				val pi = new Particle(i)
				val pj = new Particle(j)
				val dx = pi.x-pj.x
				val dy = pi.y-pj.y
				val dz = pi.z-pj.z
				val dist = math.sqrt(dx*dx+dy*dy+dz*dz)
				val magi = pj.mass/(dist*dist*dist)
				pi.ax -= magi*dx
				pi.ay -= magi*dy
				pi.az -= magi*dz
				val magj = pi.mass/(dist*dist*dist)
				pj.ax += magj*dx
				pj.ay += magj*dy
				pj.az += magj*dz
			}
			for(i <- 0 until numBodies) {
				val p = new Particle(i)
				p.vx += p.ax*dt
				p.vy += p.ay*dt
				p.vz += p.az*dt
				p.x += p.vx*dt
				p.y += p.vy*dt
				p.z += p.vz*dt
			}
		}
	}

	def whileSim(steps: Int): Unit = {
		var s = 0
		while(s < steps) {
			var i = 0
			while(i < numBodies*3) {
				accel(i) = 0.0
				i += 1
			}
			i = 0
			while (i < numBodies) {
				val pi = new Particle(i)
				var j = i+1
				while(j < numBodies) {
					val pj = new Particle(j)
					val dx = pi.x-pj.x
					val dy = pi.y-pj.y
					val dz = pi.z-pj.z
					val dist = math.sqrt(dx*dx+dy*dy+dz*dz)
					val magi = pj.mass/(dist*dist*dist)
					pi.ax -= magi*dx
					pi.ay -= magi*dy
					pi.az -= magi*dz
					val magj = pi.mass/(dist*dist*dist)
					pj.ax += magj*dx
					pj.ay += magj*dy
					pj.az += magj*dz
					j += 1
				}
				i += 1
			}
			i = 0
			while(i < numBodies) {
				val p = new Particle(i)
				p.vx += p.ax*dt
				p.vy += p.ay*dt
				p.vz += p.az*dt
				p.x += p.vx*dt
				p.y += p.vy*dt
				p.z += p.vz*dt
				i += 1
			}
			s += 1
		}
	}
}

